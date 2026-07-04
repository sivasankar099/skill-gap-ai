package com.skillgap.ai.utils;

import com.skillgap.ai.entity.RoleSkill;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ScoreCalculator {

    public static class Result {
        private final double score;
        private final List<String> strongSkills;
        private final List<String> missingSkills;

        public Result(double score, List<String> strongSkills, List<String> missingSkills) {
            this.score = score;
            this.strongSkills = strongSkills;
            this.missingSkills = missingSkills;
        }

        public double getScore() {
            return score;
        }

        public List<String> getStrongSkills() {
            return strongSkills;
        }

        public List<String> getMissingSkills() {
            return missingSkills;
        }
    }

    public static Result calculate(List<RoleSkill> requiredSkills, List<String> userSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty()) {
            return new Result(100.0, new ArrayList<>(), new ArrayList<>());
        }

        // Normalize user skills for comparison (lowercase, trimmed)
        List<String> normalizedUserSkills = userSkills.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        List<String> strongSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        int totalWeight = 0;
        int earnedWeight = 0;

        for (RoleSkill reqSkill : requiredSkills) {
            String rawSkillName = reqSkill.getSkillName();
            int weight = reqSkill.getImportance().getWeight();
            totalWeight += weight;

            String normalizedReqSkill = rawSkillName.trim().toLowerCase();
            boolean isMatched = false;

            // Flexible matching to catch variants (e.g. "React JS" matches "React")
            for (String userSkill : normalizedUserSkills) {
                if (userSkill.contains(normalizedReqSkill) || normalizedReqSkill.contains(userSkill)) {
                    isMatched = true;
                    break;
                }
            }

            if (isMatched) {
                strongSkills.add(rawSkillName);
                earnedWeight += weight;
            } else {
                missingSkills.add(rawSkillName);
            }
        }

        double score = totalWeight > 0 ? ((double) earnedWeight / totalWeight) * 100.0 : 0.0;
        score = Math.round(score * 100.0) / 100.0; // Round to 2 decimal places

        return new Result(score, strongSkills, missingSkills);
    }
}
