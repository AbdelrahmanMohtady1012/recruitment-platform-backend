package com.recruitment.candidate.dto;

import com.recruitment.candidate.entity.Candidate;
import com.recruitment.candidate.entity.CandidateStatus;

import java.time.LocalDateTime;
import java.util.List;

public class CandidateResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String cvPath;
    private CandidateStatus status;
    private LocalDateTime createdAt;
    private List<String> tags;

    public CandidateResponse(Candidate candidate) {
        this.id = candidate.getId();
        this.firstName = candidate.getFirstName();
        this.lastName = candidate.getLastName();
        this.email = candidate.getEmail();
        this.phone = candidate.getPhone();
        this.cvPath = candidate.getCvPath();
        this.status = candidate.getStatus();
        this.createdAt = candidate.getCreatedAt();
        this.tags = candidate.getTags();
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getCvPath() {
        return cvPath;
    }

    public CandidateStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<String> getTags() {
        return tags;
    }
}