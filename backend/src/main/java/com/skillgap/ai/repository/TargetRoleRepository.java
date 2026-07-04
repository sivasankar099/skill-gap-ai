package com.skillgap.ai.repository;

import com.skillgap.ai.entity.TargetRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TargetRoleRepository extends JpaRepository<TargetRole, Long> {
    Optional<TargetRole> findByName(String name);
}
