package com.recruitment.auth.controller;

import com.recruitment.auth.dto.RegisterRequest;
import com.recruitment.auth.entity.User;
import com.recruitment.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.recruitment.auth.dto.UserResponse;
import com.recruitment.auth.dto.LoginRequest;
import com.recruitment.auth.dto.LoginResponse;
import com.recruitment.security.JwtService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        UserResponse response = new UserResponse(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @SecurityRequirements
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        User user = authService.login(request);

        String token = jwtService.generateToken(user);

        LoginResponse response = new LoginResponse(user, token);

        return ResponseEntity.ok(response);
    }
}