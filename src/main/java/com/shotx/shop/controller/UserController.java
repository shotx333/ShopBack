package com.shotx.shop.controller;

import com.shotx.shop.model.ChangePasswordRequest;
import com.shotx.shop.model.UpdateProfileRequest;
import com.shotx.shop.model.Users;
import com.shotx.shop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    /* ─── Password ──────────────────────────────────────────────────────── */

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(Authentication auth,
                                                 @Valid @RequestBody ChangePasswordRequest r) {

        service.changePassword(auth.getName(), r.getOldPassword(), r.getNewPassword());
        return ResponseEntity.ok("Password changed successfully");
    }

    /* ─── Profile CRUD ──────────────────────────────────────────────────── */

    @GetMapping("/me")
    public Users me(Authentication auth) {
        return service.getProfile(auth.getName());
    }

    @PutMapping("/me")
    public Users updateMe(Authentication auth,
                          @Valid @RequestBody UpdateProfileRequest req) {
        return service.updateProfile(auth.getName(), req);
    }

    /* avatar upload (multipart/form-data: file=…) */
    @PostMapping("/me/avatar")
    public ResponseEntity<String> uploadAvatar(Authentication auth,
                                               @RequestPart("file") MultipartFile file)
            throws Exception {

        String url = service.uploadAvatar(auth.getName(), file);
        return ResponseEntity.ok(url);
    }
}
