package com.recruitment.auth.service;

import com.recruitment.auth.dto.LoginRequest;
import com.recruitment.auth.dto.RegisterRequest;
import com.recruitment.auth.entity.User;
import com.recruitment.auth.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LdapAuthenticationService ldapAuthenticationService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            LdapAuthenticationService ldapAuthenticationService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.ldapAuthenticationService = ldapAuthenticationService;
    }

    public User register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already registered"
            );
        }

        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getName(),
                request.getEmail(),
                encodedPassword,
                request.getRole()
        );

        return userRepository.save(user);
    }

    public User login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid email or password"
                ));

        boolean authenticated =
                ldapAuthenticationService.authenticate(
                        request.getEmail(),
                        request.getPassword()
                );

        if (!authenticated) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }

        return user;
    }
}