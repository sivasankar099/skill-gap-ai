package com.skillgap.ai.repository;

import com.skillgap.ai.entity.RoleSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleSkillRepository extends JpaRepository<RoleSkill, Long> {
    List<RoleSkill> findByTargetRoleId(Long roleId);
}
