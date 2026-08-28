package com.recruitment.candidate.controller;

import com.recruitment.candidate.dto.CandidateRequest;
import com.recruitment.candidate.dto.CandidateResponse;
import com.recruitment.candidate.entity.Candidate;
import com.recruitment.candidate.service.CandidateService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @PostMapping
    public ResponseEntity<CandidateResponse> createCandidate(
            @Valid @RequestBody CandidateRequest request) {

        Candidate candidate = candidateService.createCandidate(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new CandidateResponse(candidate));
    }

    @GetMapping
    public ResponseEntity<List<CandidateResponse>> getAllCandidates() {

        List<CandidateResponse> candidates = candidateService
                .getAllCandidates()
                .stream()
                .map(CandidateResponse::new)
                .toList();

        return ResponseEntity.ok(candidates);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CandidateResponse> getCandidateById(
            @PathVariable Long id) {

        Candidate candidate = candidateService.getCandidateById(id);

        return ResponseEntity.ok(
                new CandidateResponse(candidate)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CandidateResponse> updateCandidate(
            @PathVariable Long id,
            @Valid @RequestBody CandidateRequest request) {

        Candidate candidate =
                candidateService.updateCandidate(id, request);

        return ResponseEntity.ok(
                new CandidateResponse(candidate)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidate(
            @PathVariable Long id) {

        candidateService.deleteCandidate(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<CandidateResponse>> searchCandidates(
            @RequestParam String name) {

        List<CandidateResponse> candidates = candidateService
                .searchCandidatesByName(name)
                .stream()
                .map(CandidateResponse::new)
                .toList();

        return ResponseEntity.ok(candidates);
    }

    @PostMapping("/{id}/tags")
    public ResponseEntity<CandidateResponse> addTag(
            @PathVariable Long id,
            @RequestParam String tag) {

        Candidate candidate =
                candidateService.addTag(id, tag);

        return ResponseEntity.ok(
                new CandidateResponse(candidate)
        );
    }

    @GetMapping("/search/tag")
    public ResponseEntity<List<CandidateResponse>> searchCandidatesByTag(
            @RequestParam String tag) {

        List<CandidateResponse> candidates = candidateService
                .searchCandidatesByTag(tag)
                .stream()
                .map(CandidateResponse::new)
                .toList();

        return ResponseEntity.ok(candidates);
    }

    @PostMapping("/{id}/cv")
    public ResponseEntity<CandidateResponse> uploadCv(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file)
            throws IOException {

        Candidate candidate =
                candidateService.uploadCv(id, file);

        return ResponseEntity.ok(
                new CandidateResponse(candidate)
        );
    }

    @GetMapping("/{id}/cv/text")
    public ResponseEntity<String> getCvText(
            @PathVariable Long id)
            throws IOException {

        String text = candidateService.extractCvText(id);

        return ResponseEntity.ok(text);
    }

    @PostMapping("/{id}/cv/parse")
    public ResponseEntity<CandidateResponse> parseCv(
            @PathVariable Long id)
            throws IOException {

        Candidate candidate =
                candidateService.parseCv(id);

        return ResponseEntity.ok(
                new CandidateResponse(candidate)
        );
    }

    @PostMapping("/cv/bulk")
    public ResponseEntity<List<CandidateResponse>> uploadMultipleCvs(
            @RequestParam("candidateIds") List<Long> candidateIds,
            @RequestParam("files") MultipartFile[] files)
            throws IOException {

        List<CandidateResponse> candidates = candidateService
                .uploadMultipleCvs(candidateIds, files)
                .stream()
                .map(CandidateResponse::new)
                .toList();

        return ResponseEntity.ok(candidates);
    }
}