package com.recruitment.tracking.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "job_applications")
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long candidateId;

    private String jobTitle;

    private Long recruiterId;

    @Column(name = "isDeleted", nullable = false)
    private boolean deleted = false;

    private Long interviewerId;

    @Enumerated(EnumType.STRING)
    private ApplicationStage stage;

    private LocalDateTime createdAt;

    private String feedback;

    private Integer evaluationScore;

    public JobApplication() {
    }

    public JobApplication(Long candidateId, String jobTitle) {
        this.candidateId = candidateId;
        this.jobTitle = jobTitle;
        this.stage = ApplicationStage.APPLIED;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public ApplicationStage getStage() {
        return stage;
    }

    public void setStage(ApplicationStage stage) {
        this.stage = stage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Long getRecruiterId() {
        return recruiterId;
    }

    public void setRecruiterId(Long recruiterId) {
        this.recruiterId = recruiterId;
    }

    public Long getInterviewerId() {
        return interviewerId;
    }

    public void setInterviewerId(Long interviewerId) {
        this.interviewerId = interviewerId;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Integer getEvaluationScore() {
        return evaluationScore;
    }

    public void setEvaluationScore(Integer evaluationScore) {
        this.evaluationScore = evaluationScore;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}