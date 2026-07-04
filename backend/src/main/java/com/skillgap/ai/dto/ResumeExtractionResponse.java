package com.skillgap.ai.dto;

import java.util.List;

public record ResumeExtractionResponse(
    List<String> technicalSkills,
    List<String> softSkills,
    List<ProjectDTO> projects,
    List<String> experience,
    List<String> education
) {}
