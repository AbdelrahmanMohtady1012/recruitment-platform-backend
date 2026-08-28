package com.recruitment.auth.dto;

import com.recruitment.auth.entity.Role;
import com.recruitment.auth.entity.User;

public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;

    public UserResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
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
}