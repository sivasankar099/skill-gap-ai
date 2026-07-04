package com.skillgap.ai.service;

import com.skillgap.ai.entity.TargetRole;
import com.skillgap.ai.repository.TargetRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleService {

    private final TargetRoleRepository targetRoleRepository;

    public RoleService(TargetRoleRepository targetRoleRepository) {
        this.targetRoleRepository = targetRoleRepository;
    }

    @Transactional(readOnly = true)
    public List<TargetRole> getAllRoles() {
        return targetRoleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public TargetRole getRoleById(Long id) {
        return targetRoleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Target role not found with ID: " + id));
    }
}
