package com.shotx.shop.controller;

import com.shotx.shop.model.UserAuthRequest;
import com.shotx.shop.model.Users;
import com.shotx.shop.service.AuthService;
import com.shotx.shop.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    private final AuthService authService;

    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    // Endpoint to register a new user
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserAuthRequest userAuthRequest) {
        try {
            Users user = userService.registerUser(userAuthRequest.getUsername(), userAuthRequest.getPassword());
            return ResponseEntity.ok("User registered with id: " + user.getId());
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
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
}
