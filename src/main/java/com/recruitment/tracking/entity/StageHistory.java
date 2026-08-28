package com.recruitment.tracking.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stage_history")
public class StageHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long applicationId;

    @Enumerated(EnumType.STRING)
    private ApplicationStage oldStage;

    @Enumerated(EnumType.STRING)
    private ApplicationStage newStage;

    private LocalDateTime changedAt;

    public StageHistory() {
    }

    public StageHistory(Long applicationId,
                        ApplicationStage oldStage,
                        ApplicationStage newStage) {
        this.applicationId = applicationId;
        this.oldStage = oldStage;
        this.newStage = newStage;
        this.changedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public ApplicationStage getOldStage() {
        return oldStage;
    }

    public ApplicationStage getNewStage() {
        return newStage;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }
}