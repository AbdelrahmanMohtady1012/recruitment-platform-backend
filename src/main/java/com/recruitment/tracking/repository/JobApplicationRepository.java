package com.recruitment.tracking.repository;

import com.recruitment.tracking.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository
        extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findByDeletedFalse();

    Optional<JobApplication> findByIdAndDeletedFalse(Long id);
}