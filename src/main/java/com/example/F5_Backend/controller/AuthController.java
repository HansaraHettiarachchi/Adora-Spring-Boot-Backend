package com.example.F5_Backend.controller;

import com.example.F5_Backend.entities.Users;
import com.example.F5_Backend.repo.UsersRepo;
import com.example.F5_Backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UsersRepo usersRepo;

    /**
     * @route POST /api/auth/login
     * @description Authenticate user and return JWT token
     * @access Public
     * @body { "email": string, "password": string }
     * @response { "token": string } on success
     *           { "status": 401, "message": "Invalid credentials" } on error
     */
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            Users user = usersRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            String token = JwtUtil.createAccessToken(user);
            return Map.of("token", token);
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid credentials");
        }
    }
}
