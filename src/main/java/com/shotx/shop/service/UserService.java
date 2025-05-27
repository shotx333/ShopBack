package com.shotx.shop.service;

import com.shotx.shop.model.UpdateProfileRequest;
import com.shotx.shop.model.Users;
import com.shotx.shop.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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

    public Users registerUser(String username, String email, String password) {
        if (userRepository.findByUsername(username) != null) {
            throw new RuntimeException("User already exists");
        }
        Users u = new Users();
        u.setUsername(username);
        u.setEmail(email);
        u.setPassword(encoder.encode(password));
        return userRepository.save(u);
    }

    public Users authenticate(String username, String rawPassword) {
        Users u = userRepository.findByUsername(username);
        return (u != null && encoder.matches(rawPassword, u.getPassword())) ? u : null;
    }

    /* ─── Password ───────────────────────────────────────────────────────── */

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

    public String uploadAvatar(String username, MultipartFile file) throws IOException {
        Users u = userRepository.findByUsername(username);
        if (u == null) throw new RuntimeException("User not found");

        String ext = switch (file.getContentType()) {
            case "image/png"  -> ".png";
            case "image/jpeg" -> ".jpg";
            default -> throw new RuntimeException("Only PNG or JPEG allowed");
        };

        String filename = username + "_" + System.currentTimeMillis() + ext;
        File target = new File(AVATAR_DIR, filename);
        Files.copy(file.getInputStream(), target.toPath());

        u.setAvatarUrl("/" + AVATAR_DIR + "/" + filename);
        userRepository.save(u);
        return u.getAvatarUrl();
    }
}
