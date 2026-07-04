package com.skillgap.ai.dto;

import jakarta.validation.constraints.NotNull;

public record AnalysisRequest(
    @NotNull(message = "Target role ID cannot be null")
    Long targetRoleId
) {}
