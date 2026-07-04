package com.skillgap.ai.dto;

public record LearningResourceDTO(
    Long id,
    String title,
    String description,
    String url,
    String resourceType,
    String skillName
) {}
