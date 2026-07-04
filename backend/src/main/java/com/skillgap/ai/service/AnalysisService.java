package com.skillgap.ai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillgap.ai.ai.GeminiClient;
import com.skillgap.ai.dto.AnalysisResponse;
import com.skillgap.ai.dto.LearningResourceDTO;
import com.skillgap.ai.dto.ResumeExtractionResponse;
import com.skillgap.ai.entity.*;
import com.skillgap.ai.repository.*;
import com.skillgap.ai.utils.ScoreCalculator;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AnalysisService {

    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final TargetRoleRepository targetRoleRepository;
    private final SkillAnalysisRepository skillAnalysisRepository;
    private final GeminiClient geminiClient;
    private final RecommendationService recommendationService;
    private final ObjectMapper objectMapper;

    public AnalysisService(UserRepository userRepository,
                           ResumeRepository resumeRepository,
                           TargetRoleRepository targetRoleRepository,
                           SkillAnalysisRepository skillAnalysisRepository,
                           GeminiClient geminiClient,
                           RecommendationService recommendationService,
                           ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.resumeRepository = resumeRepository;
        this.targetRoleRepository = targetRoleRepository;
        this.skillAnalysisRepository = skillAnalysisRepository;
        this.geminiClient = geminiClient;
        this.recommendationService = recommendationService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public AnalysisResponse runAnalysis(String username, Long targetRoleId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        Resume resume = resumeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("No resume found. Please upload a PDF resume before running analysis."));

        TargetRole targetRole = targetRoleRepository.findById(targetRoleId)
                .orElseThrow(() -> new IllegalArgumentException("Target role not found with ID: " + targetRoleId));

        try {
            // 1. Normalize and Extract Resume details using Gemini
            ResumeExtractionResponse extraction = geminiClient.extractResumeData(resume.getParsedText());

            // Combine technical and soft skills for matching
            List<String> userSkills = new ArrayList<>();
            if (extraction.technicalSkills() != null) userSkills.addAll(extraction.technicalSkills());
            if (extraction.softSkills() != null) userSkills.addAll(extraction.softSkills());

            // 2. Perform deterministic score calculations in Java
            ScoreCalculator.Result calcResult = ScoreCalculator.calculate(targetRole.getRequiredSkills(), userSkills);

            // Serialize lists to JSON strings for database caching
            String strongSkillsJson = objectMapper.writeValueAsString(calcResult.getStrongSkills());
            String missingSkillsJson = objectMapper.writeValueAsString(calcResult.getMissingSkills());

            // 3. Ask Gemini to generate the personalized roadmap and explanation (Cached per run)
            String rawRoadmapJson = geminiClient.generateRoadmapAndExplanation(
                    targetRole.getName(),
                    missingSkillsJson,
                    strongSkillsJson
            );

            // Extract the gap explanation text and save the raw JSON structure
            Object parsedRoadmap = objectMapper.readValue(rawRoadmapJson, Object.class);
            String gapExplanation = objectMapper.readTree(rawRoadmapJson).path("gapExplanation").asText("No explanation generated.");

            // 4. Save analysis details to MySQL
            SkillAnalysis analysis = new SkillAnalysis(
                    user,
                    targetRole,
                    calcResult.getScore(),
                    strongSkillsJson,
                    missingSkillsJson,
                    gapExplanation,
                    rawRoadmapJson
            );
            skillAnalysisRepository.save(analysis);

            // 5. Query Qdrant for learning resources matching missing skills
            List<LearningResource> recommendations = recommendationService.getRecommendations(calcResult.getMissingSkills());
            List<LearningResourceDTO> recommendationDTOs = recommendations.stream()
                    .map(r -> new LearningResourceDTO(r.getId(), r.getTitle(), r.getDescription(), r.getUrl(), r.getResourceType(), r.getSkillName()))
                    .collect(Collectors.toList());

            return new AnalysisResponse(
                    analysis.getId(),
                    targetRole.getName(),
                    analysis.getSkillScore(),
                    calcResult.getStrongSkills(),
                    calcResult.getMissingSkills(),
                    gapExplanation,
                    parsedRoadmap,
                    recommendationDTOs,
                    analysis.getAnalyzedAt()
            );

        } catch (Exception ex) {
            throw new RuntimeException("Failed to run skill gap analysis: " + ex.getMessage(), ex);
        }
    }

    @Transactional(readOnly = true)
    public Optional<AnalysisResponse> getLatestAnalysis(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        Optional<SkillAnalysis> latestAnalysisOpt = skillAnalysisRepository.findTopByUserIdOrderByAnalyzedAtDesc(user.getId());
        if (latestAnalysisOpt.isEmpty()) {
            return Optional.empty();
        }

        SkillAnalysis analysis = latestAnalysisOpt.get();

        try {
            List<String> strongSkills = objectMapper.readValue(analysis.getStrongSkills(), new TypeReference<List<String>>() {});
            List<String> missingSkills = objectMapper.readValue(analysis.getMissingSkills(), new TypeReference<List<String>>() {});
            Object roadmap = objectMapper.readValue(analysis.getRoadmap(), Object.class);

            List<LearningResource> recommendations = recommendationService.getRecommendations(missingSkills);
            List<LearningResourceDTO> recommendationDTOs = recommendations.stream()
                    .map(r -> new LearningResourceDTO(r.getId(), r.getTitle(), r.getDescription(), r.getUrl(), r.getResourceType(), r.getSkillName()))
                    .collect(Collectors.toList());

            return Optional.of(new AnalysisResponse(
                    analysis.getId(),
                    analysis.getTargetRole().getName(),
                    analysis.getSkillScore(),
                    strongSkills,
                    missingSkills,
                    analysis.getGapExplanation(),
                    roadmap,
                    recommendationDTOs,
                    analysis.getAnalyzedAt()
            ));
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load cached skill gap analysis: " + ex.getMessage(), ex);
        }
    }
}
