package com.skillgap.ai.controller;

import com.skillgap.ai.dto.ResumeExtractionResponse;
import com.skillgap.ai.service.ResumeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Uploaded file is empty");
            return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
        }

        // Validate that it is a PDF file
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        if ((contentType != null && !contentType.equalsIgnoreCase("application/pdf")) &&
            (originalFilename != null && !originalFilename.toLowerCase().endsWith(".pdf"))) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Only PDF resumes are supported");
            return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
        }

        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            ResumeExtractionResponse response = resumeService.parseAndExtract(
                    username,
                    file.getOriginalFilename(),
                    file.getBytes()
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            Map<String, String> err = new HashMap<>();
            err.put("error", ex.getMessage());
            return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
        } catch (IOException ex) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Error parsing PDF document: " + ex.getMessage());
            return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            Map<String, String> err = new HashMap<>();
            err.put("error", ex.getMessage());
            return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
