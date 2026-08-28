package com.recruitment.auth.dto;

import com.recruitment.auth.entity.Role;
import com.recruitment.auth.entity.User;

public class LoginResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private String token;

    public LoginResponse(User user, String token) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public String getToken() {
        return token;
    }
}