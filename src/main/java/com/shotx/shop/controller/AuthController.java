package com.shotx.shop.controller;

import com.shotx.shop.model.RegisterRequest;
import com.shotx.shop.model.UserAuthRequest;
import com.shotx.shop.model.Users;
import com.shotx.shop.service.AuthService;
import com.shotx.shop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    private final UserService userService;

    private final AuthService authService;

    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest req){
        userService.register(req);
        return ResponseEntity.ok().build();
    }

    // Endpoint to login a user and return an auth token
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserAuthRequest userAuthRequest) {
        Users user = userService.authenticate(userAuthRequest.getUsername(), userAuthRequest.getPassword());
        if (user != null) {
            String token = authService.generateToken(user);
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.badRequest().body("Invalid credentials");
    }

    // Endpoint to logout a user (token revocation)
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        authService.revokeToken(token);
        return ResponseEntity.ok("Logged out successfully");
    }
    @GetMapping("/admin-check")
    public ResponseEntity<?> adminCheck(Authentication authentication) {
        // Assuming you have a User entity with a role property:
        Users user = userService.findByUsername(authentication.getName());
        if (user != null && "ADMIN".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not an admin");
    }

}
