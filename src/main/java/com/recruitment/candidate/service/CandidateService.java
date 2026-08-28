package com.recruitment.candidate.service;

import com.recruitment.candidate.dto.CandidateRequest;
import com.recruitment.candidate.entity.Candidate;
import com.recruitment.candidate.entity.CandidateStatus;
import com.recruitment.candidate.repository.CandidateRepository;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;

    public CandidateService(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    public Candidate createCandidate(CandidateRequest request) {

        if (candidateRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Candidate email already exists"
            );
        }

        Candidate candidate = new Candidate(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPhone()
        );

        return candidateRepository.save(candidate);
    }

    public List<Candidate> getAllCandidates() {

        return candidateRepository.findByDeletedFalse();
    }

    public Candidate getCandidateById(Long id) {
        return candidateRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidate not found"
                ));
    }

    public Candidate updateCandidate(Long id, CandidateRequest request) {

        Candidate candidate = getCandidateById(id);

        if (!candidate.getEmail().equals(request.getEmail())
                && candidateRepository.existsByEmail(request.getEmail())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Candidate email already exists"
            );
        }

        candidate.setFirstName(request.getFirstName());
        candidate.setLastName(request.getLastName());
        candidate.setEmail(request.getEmail());
        candidate.setPhone(request.getPhone());

        return candidateRepository.save(candidate);
    }

    public void deleteCandidate(Long id) {

        Candidate candidate = getCandidateById(id);

        candidate.setDeleted(true);

        candidateRepository.save(candidate);
    }

    public List<Candidate> searchCandidatesByName(String name) {

        return candidateRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                        name,
                        name
                )
                .stream()
                .filter(candidate -> !candidate.isDeleted())
                .toList();
    }

    public Candidate addTag(Long id, String tag) {
        Candidate candidate = getCandidateById(id);

        if (!candidate.getTags().contains(tag)) {
            candidate.getTags().add(tag);
        }

        return candidateRepository.save(candidate);
    }

    public List<Candidate> searchCandidatesByTag(String tag) {
        return candidateRepository.findByTagsContainingAndDeletedFalse(tag);
    }

    public Candidate uploadCv(Long id, MultipartFile file) throws IOException {

        Candidate candidate = getCandidateById(id);

        String uploadDir = "uploads/cvs/";

        Files.createDirectories(Paths.get(uploadDir));

        String fileName = id + "_" + file.getOriginalFilename();

        Path filePath = Paths.get(uploadDir + fileName);

        Files.write(filePath, file.getBytes());

        candidate.setCvPath(filePath.toString());

        return candidateRepository.save(candidate);
    }

    public String extractCvText(Long id) throws IOException {

        Candidate candidate = getCandidateById(id);

        if (candidate.getCvPath() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Candidate has no CV"
            );
        }

        File file = new File(candidate.getCvPath());

        try (PDDocument document = Loader.loadPDF(file)) {

            PDFTextStripper stripper = new PDFTextStripper();

            return stripper.getText(document);
        }
    }

    public Candidate parseCv(Long id) throws IOException {

        Candidate candidate = getCandidateById(id);

        String cvText = extractCvText(id).toLowerCase();

        List<String> skills = List.of(
                "Java",
                "Spring",
                "SQL",
                "Python",
                "React",
                "JavaScript"
        );

        for (String skill : skills) {

            if (cvText.contains(skill.toLowerCase())
                    && !candidate.getTags().contains(skill)) {

                candidate.getTags().add(skill);
            }
        }

        return candidateRepository.save(candidate);
    }

    public void updateStatus(Long id, CandidateStatus status) {

        Candidate candidate = getCandidateById(id);

        candidate.setStatus(status);

        candidateRepository.save(candidate);
    }

    public List<Candidate> uploadMultipleCvs(
            List<Long> candidateIds,
            MultipartFile[] files) throws IOException {

        if (candidateIds.size() != files.length) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Number of candidate IDs must match number of files"
            );
        }

        List<Candidate> updatedCandidates = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {

            Candidate candidate = uploadCv(
                    candidateIds.get(i),
                    files[i]
            );

            updatedCandidates.add(candidate);
        }

        return updatedCandidates;
    }
}