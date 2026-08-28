package com.recruitment.tracking.service;

import com.recruitment.auth.entity.Role;
import com.recruitment.auth.entity.User;
import com.recruitment.auth.repository.UserRepository;
import com.recruitment.candidate.entity.CandidateStatus;
import com.recruitment.candidate.service.CandidateService;
import com.recruitment.tracking.dto.JobApplicationRequest;
import com.recruitment.tracking.entity.ApplicationStage;
import com.recruitment.tracking.entity.JobApplication;
import com.recruitment.tracking.entity.StageHistory;
import com.recruitment.tracking.repository.JobApplicationRepository;
import com.recruitment.tracking.repository.StageHistoryRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final StageHistoryRepository stageHistoryRepository;
    private final CandidateService candidateService;
    private final UserRepository userRepository;

    public JobApplicationService(
            JobApplicationRepository jobApplicationRepository,
            StageHistoryRepository stageHistoryRepository,
            CandidateService candidateService,
            UserRepository userRepository) {

        this.jobApplicationRepository = jobApplicationRepository;
        this.stageHistoryRepository = stageHistoryRepository;
        this.candidateService = candidateService;
        this.userRepository = userRepository;
    }

    public JobApplication createApplication(JobApplicationRequest request) {

        candidateService.getCandidateById(request.getCandidateId());

        JobApplication application = new JobApplication(
                request.getCandidateId(),
                request.getJobTitle()
        );

        return jobApplicationRepository.save(application);
    }

    public JobApplication updateStage(Long id, ApplicationStage stage) {

        JobApplication application = getApplicationById(id);

        ApplicationStage oldStage = application.getStage();

        application.setStage(stage);

        JobApplication updatedApplication =
                jobApplicationRepository.save(application);

        StageHistory history = new StageHistory(
                application.getId(),
                oldStage,
                stage
        );

        stageHistoryRepository.save(history);

        if (stage == ApplicationStage.HIRED) {
            candidateService.updateStatus(
                    application.getCandidateId(),
                    CandidateStatus.HIRED
            );
        }

        if (stage == ApplicationStage.REJECTED) {
            candidateService.updateStatus(
                    application.getCandidateId(),
                    CandidateStatus.DISQUALIFIED
            );
        }

        return updatedApplication;
    }

    public List<StageHistory> getStageHistory(Long applicationId) {

        getApplicationById(applicationId);

        return stageHistoryRepository.findByApplicationId(applicationId);
    }

    public JobApplication assignRecruiter(Long id, Long recruiterId) {

        JobApplication application = getApplicationById(id);

        User recruiter = getUserById(recruiterId);

        if (recruiter.getRole() != Role.HR) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Recruiter must be an HR user"
            );
        }

        application.setRecruiterId(recruiterId);

        return jobApplicationRepository.save(application);
    }

    public JobApplication assignInterviewer(Long id, Long interviewerId) {

        JobApplication application = getApplicationById(id);

        User interviewer = getUserById(interviewerId);

        if (interviewer.getRole() != Role.INTERVIEWER) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Interviewer must have INTERVIEWER role"
            );
        }

        application.setInterviewerId(interviewerId);

        return jobApplicationRepository.save(application);
    }

    public JobApplication addFeedback(
            Long id,
            String feedback,
            Integer score) {

        JobApplication application = getApplicationById(id);

        if (score == null || score < 1 || score > 10) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Evaluation score must be between 1 and 10"
            );
        }

        application.setFeedback(feedback);
        application.setEvaluationScore(score);

        return jobApplicationRepository.save(application);
    }

    public List<JobApplication> getAllApplications() {

        return jobApplicationRepository.findByDeletedFalse();
    }

    public JobApplication getApplicationById(Long id) {

        return jobApplicationRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Application not found"
                ));
    }

    private User getUserById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));
    }

    public void deleteApplication(Long id){

        JobApplication application = getApplicationById(id);

        application.setDeleted(true);

        jobApplicationRepository.save(application);
    }
}