package com.shotx.shop.service;

import com.shotx.shop.model.Users;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    private final RedisTemplate<String, String> redisTemplate;

    public AuthService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String generateToken(Users user) {
        String token = UUID.randomUUID().toString();
        // Store the token with the associated username in Redis (expires in 1 hour)
        redisTemplate.opsForValue().set(token, user.getUsername(), 1, TimeUnit.HOURS);
        return token;
    }

    public void revokeToken(String token) {
        redisTemplate.delete(token);
    }
}
