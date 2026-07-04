package com.skillgap.ai.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "role_skills", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"role_id", "skill_name"})
})
public class RoleSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    @JsonIgnore
    private TargetRole targetRole;

    @Column(name = "skill_name", nullable = false, length = 100)
    private String skillName;

    @Enumerated(EnumType.STRING)
    @Column(name = "skill_type", nullable = false, length = 20)
    private SkillType skillType;

    @Enumerated(EnumType.STRING)
    @Column(name = "importance", nullable = false, length = 20)
    private Importance importance;

    // Constructors
    public RoleSkill() {
    }

    public RoleSkill(TargetRole targetRole, String skillName, SkillType skillType, Importance importance) {
        this.targetRole = targetRole;
        this.skillName = skillName;
        this.skillType = skillType;
        this.importance = importance;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TargetRole getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(TargetRole targetRole) {
        this.targetRole = targetRole;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public SkillType getSkillType() {
        return skillType;
    }

    public void setSkillType(SkillType skillType) {
        this.skillType = skillType;
    }

    public Importance getImportance() {
        return importance;
    }

    public void setImportance(Importance importance) {
        this.importance = importance;
    }
}
