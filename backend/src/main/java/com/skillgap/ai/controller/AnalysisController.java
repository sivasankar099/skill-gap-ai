package com.skillgap.ai.controller;

import com.skillgap.ai.dto.AnalysisRequest;
import com.skillgap.ai.dto.AnalysisResponse;
import com.skillgap.ai.service.AnalysisService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/analyses")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping("/run")
    public ResponseEntity<?> runAnalysis(@Valid @RequestBody AnalysisRequest request) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            AnalysisResponse response = analysisService.runAnalysis(username, request.targetRoleId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            Map<String, String> err = new HashMap<>();
            err.put("error", ex.getMessage());
            return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Failed to calculate skill gap: " + ex.getMessage());
            return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/latest")
    public ResponseEntity<?> getLatestAnalysis() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<AnalysisResponse> responseOpt = analysisService.getLatestAnalysis(username);

            if (responseOpt.isPresent()) {
                return ResponseEntity.ok(responseOpt.get());
            } else {
                Map<String, String> msg = new HashMap<>();
                msg.put("message", "No previous analysis found. Please run a new analysis.");
                return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Failed to retrieve analysis: " + ex.getMessage());
            return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
