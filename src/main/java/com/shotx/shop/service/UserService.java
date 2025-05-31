package com.shotx.shop.service;

import com.shotx.shop.model.RegisterRequest;
import com.shotx.shop.model.UpdateProfileRequest;
import com.shotx.shop.model.Users;
import com.shotx.shop.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private static final String AVATAR_DIR = "uploads/avatars";

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        new File(AVATAR_DIR).mkdirs();                   // make sure dir exists
    }

    /* ─── Registration / Auth ─────────────────────────────────────────────── */

    public Users register(RegisterRequest r) {
        if (userRepository.findByUsername(r.getUsername()) != null) {
            throw new RuntimeException("User already exists");
        }

        Users u = new Users();
        u.setUsername   (r.getUsername());
        u.setEmail      (r.getEmail());
        u.setPassword   (encoder.encode(r.getPassword()));

        u.setFirstName  (r.getFirstName());
        u.setLastName   (r.getLastName());
        u.setPhoneNumber(r.getPhoneNumber());
        u.setGender     (r.getGender());
        u.setBirthYear  (r.getBirthYear());

        return userRepository.save(u);
    }

    public Users authenticate(String username, String rawPassword) {
        Users u = userRepository.findByUsername(username);
        return (u != null && encoder.matches(rawPassword, u.getPassword())) ? u : null;
    }

    /* ─── Password ─── ────────────────────────────────────────────────────── */

    public void changePassword(String username, String oldPwd, String newPwd) {
        Users u = userRepository.findByUsername(username);
        if (u == null)                    throw new RuntimeException("User not found");
        if (!encoder.matches(oldPwd, u.getPassword()))
            throw new RuntimeException("Current password is incorrect");

        u.setPassword(encoder.encode(newPwd));
        userRepository.save(u);
    }

    /* ─── Profile CRUD ───────────────────────────────────────────────────── */

    public Users getProfile(String username) {
        return userRepository.findByUsername(username);
    }

    public Users updateProfile(String username, UpdateProfileRequest req) {
        Users u = userRepository.findByUsername(username);
        if (u == null) throw new RuntimeException("User not found");

        u.setFirstName(req.getFirstName());
        u.setLastName(req.getLastName());
        u.setBirthYear(req.getBirthYear());
        u.setPhoneNumber(req.getPhoneNumber());
        u.setGender(req.getGender());

        return userRepository.save(u);
    }

    public Users findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public String uploadAvatar(String username, MultipartFile file) {

        Users u = userRepository.findByUsername(username);
        if (u == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty file");
        }

        /* ---------- derive extension (png / jpg) ---------- */
        String ext;
        String mime = file.getContentType() == null ? "" : file.getContentType();
        switch (mime) {
            case "image/png"                                -> ext = ".png";
            case "image/jpeg", "image/jpg"                  -> ext = ".jpg";
            case "application/octet-stream" /* fallback */  -> {
                String name = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
                ext = name.endsWith(".png") ? ".png" : ".jpg";
            }
            default -> throw new ResponseStatusException(
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Only PNG or JPEG allowed (got " + mime + ")");
        }

        /* ---------- ensure the directory exists & is writable ---------- */
        Path dir = Paths.get("uploads/avatars").toAbsolutePath();
        try {
            Files.createDirectories(dir);
        } catch (IOException io) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Cannot create avatar directory: " + dir, io);
        }

        /* ---------- copy the file ---------- */
        String filename = username + "_" + System.currentTimeMillis() + ext;
        Path target = dir.resolve(filename);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException io) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to write file: " + target, io);
        }

        /* ---------- save DB ---------- */
        u.setAvatarUrl("/uploads/avatars/" + filename);
        /* ── save DB & flush so we can trap SQL issues immediately ───────── */
        try {
            userRepository.saveAndFlush(u);          // flush NOW → show SQL error here
        } catch (Exception ex) {
            String msg = (ex.getCause() != null)
                    ? ex.getCause().getMessage()
                    : ex.getMessage();          // ← avoids NPE when cause is null

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Database error: " + msg, ex);
        }



        return u.getAvatarUrl();
    }

}
