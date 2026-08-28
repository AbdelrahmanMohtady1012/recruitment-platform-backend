package com.recruitment.tracking.controller;

import com.recruitment.tracking.dto.JobApplicationRequest;
import com.recruitment.tracking.entity.ApplicationStage;
import com.recruitment.tracking.entity.JobApplication;
import com.recruitment.tracking.entity.StageHistory;
import com.recruitment.tracking.service.JobApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    public JobApplicationController(JobApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }

    @PostMapping
    public ResponseEntity<JobApplication> createApplication(
            @Valid @RequestBody JobApplicationRequest request) {

        JobApplication application =
                jobApplicationService.createApplication(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(application);
    }

    @PutMapping("/{id}/stage")
    public ResponseEntity<JobApplication> updateStage(
            @PathVariable Long id,
            @RequestParam ApplicationStage stage) {

        return ResponseEntity.ok(jobApplicationService.updateStage(id, stage));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<StageHistory>> getStageHistory(@PathVariable Long id) {
        return ResponseEntity.ok(jobApplicationService.getStageHistory(id));
    }

    @PutMapping("/{id}/recruiter")
    public ResponseEntity<JobApplication> assignRecruiter(
            @PathVariable Long id,
            @RequestParam Long recruiterId) {

        return ResponseEntity.ok(jobApplicationService.assignRecruiter(id, recruiterId));
    }

    @PutMapping("/{id}/interviewer")
    public ResponseEntity<JobApplication> assignInterviewer(
            @PathVariable Long id,
            @RequestParam Long interviewerId) {

        return ResponseEntity.ok(jobApplicationService.assignInterviewer(id, interviewerId));
    }

    @PutMapping("/{id}/feedback")
    public ResponseEntity<JobApplication> addFeedback(
            @PathVariable Long id,
            @RequestParam String feedback,
            @RequestParam Integer score) {

        return ResponseEntity.ok(jobApplicationService.addFeedback(id, feedback, score));
    }

    @GetMapping
    public ResponseEntity<List<JobApplication>> getAllApplications() {
        return ResponseEntity.ok(jobApplicationService.getAllApplications());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobApplication> getApplicationById(@PathVariable Long id) {
        return ResponseEntity.ok(jobApplicationService.getApplicationById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        jobApplicationService.deleteApplication(id);
        return ResponseEntity.noContent().build();
    }
}