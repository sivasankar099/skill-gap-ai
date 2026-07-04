package com.skillgap.ai.dto;

import java.time.LocalDateTime;
import java.util.List;

public record AnalysisResponse(
    Long id,
    String roleName,
    Double skillScore,
    List<String> strongSkills,
    List<String> missingSkills,
    String gapExplanation,
    Object roadmap, // Represented as parsed JSON structure (not raw escaped string)
    List<LearningResourceDTO> learningResources,
    LocalDateTime analyzedAt
) {}
