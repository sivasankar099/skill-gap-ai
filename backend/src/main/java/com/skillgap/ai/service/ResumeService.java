package com.skillgap.ai.service;

import com.skillgap.ai.ai.GeminiClient;
import com.skillgap.ai.dto.ResumeExtractionResponse;
import com.skillgap.ai.entity.Resume;
import com.skillgap.ai.entity.User;
import com.skillgap.ai.parser.PdfResumeParser;
import com.skillgap.ai.repository.ResumeRepository;
import com.skillgap.ai.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final PdfResumeParser pdfResumeParser;
    private final GeminiClient geminiClient;

    public ResumeService(ResumeRepository resumeRepository,
                         UserRepository userRepository,
                         PdfResumeParser pdfResumeParser,
                         GeminiClient geminiClient) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.pdfResumeParser = pdfResumeParser;
        this.geminiClient = geminiClient;
    }

    @Transactional
    public ResumeExtractionResponse parseAndExtract(String username, String fileName, byte[] fileBytes) throws IOException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // 1. Extract text using PDFBox
        String parsedText = pdfResumeParser.parse(fileBytes);

        // 2. Persist text in MySQL (update existing or create new)
        saveOrUpdateResume(user, fileName, parsedText);

        // 3. Normalize & Extract structured items using Gemini
        return geminiClient.extractResumeData(parsedText);
    }

    private void saveOrUpdateResume(User user, String fileName, String parsedText) {
        Optional<Resume> existingResume = resumeRepository.findByUserId(user.getId());

        Resume resume;
        if (existingResume.isPresent()) {
            resume = existingResume.get();
            resume.setFileName(fileName);
            resume.setParsedText(parsedText);
            resume.setParsedAt(LocalDateTime.now());
        } else {
            resume = new Resume(user, fileName, parsedText);
        }

        resumeRepository.save(resume);
    }

    @Transactional(readOnly = true)
    public Optional<Resume> getResumeByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return resumeRepository.findByUserId(user.getId());
    }
}
