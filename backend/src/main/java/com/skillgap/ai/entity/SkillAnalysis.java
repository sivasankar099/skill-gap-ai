package com.skillgap.ai.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "skill_analyses")
public class SkillAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_role_id", nullable = false)
    private TargetRole targetRole;

    @Column(name = "skill_score", nullable = false)
    private Double skillScore;

    @Column(name = "strong_skills", nullable = false, columnDefinition = "JSON")
    private String strongSkills; // JSON array of strings e.g. ["Java", "Git"]

    @Column(name = "missing_skills", nullable = false, columnDefinition = "JSON")
    private String missingSkills; // JSON array of strings e.g. ["Spring Boot", "Docker"]

    @Column(name = "gap_explanation", nullable = false, columnDefinition = "TEXT")
    private String gapExplanation;

    @Column(name = "roadmap", nullable = false, columnDefinition = "JSON")
    private String roadmap; // Cached 4-week roadmap structure in JSON format

    @Column(name = "analyzed_at")
    private LocalDateTime analyzedAt;

    @PrePersist
    protected void onCreate() {
        analyzedAt = LocalDateTime.now();
    }

    // Constructors
    public SkillAnalysis() {
    }

    public SkillAnalysis(User user, TargetRole targetRole, Double skillScore, String strongSkills, String missingSkills, String gapExplanation, String roadmap) {
        this.user = user;
        this.targetRole = targetRole;
        this.skillScore = skillScore;
        this.strongSkills = strongSkills;
        this.missingSkills = missingSkills;
        this.gapExplanation = gapExplanation;
        this.roadmap = roadmap;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TargetRole getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(TargetRole targetRole) {
        this.targetRole = targetRole;
    }

    public Double getSkillScore() {
        return skillScore;
    }

    public void setSkillScore(Double skillScore) {
        this.skillScore = skillScore;
    }

    public String getStrongSkills() {
        return strongSkills;
    }

    public void setStrongSkills(String strongSkills) {
        this.strongSkills = strongSkills;
    }

    public String getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(String missingSkills) {
        this.missingSkills = missingSkills;
    }

    public String getGapExplanation() {
        return gapExplanation;
    }

    public void setGapExplanation(String gapExplanation) {
        this.gapExplanation = gapExplanation;
    }

    public String getRoadmap() {
        return roadmap;
    }

    public void setRoadmap(String roadmap) {
        this.roadmap = roadmap;
    }

    public LocalDateTime getAnalyzedAt() {
        return analyzedAt;
    }

    public void setAnalyzedAt(LocalDateTime analyzedAt) {
        this.analyzedAt = analyzedAt;
    }
}
