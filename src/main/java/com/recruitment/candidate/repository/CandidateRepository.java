package com.recruitment.candidate.repository;

import com.recruitment.candidate.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {


    boolean existsByEmail(String email);

    List<Candidate> findByDeletedFalse();

    Optional<Candidate> findByIdAndDeletedFalse(Long id);

    List<Candidate> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName,
            String lastName
    );

    List<Candidate> findByTagsContainingAndDeletedFalse(String tag);
}