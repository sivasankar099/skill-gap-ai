package com.skillgap.ai.controller;

import com.skillgap.ai.dto.RoleDTO;
import com.skillgap.ai.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        List<RoleDTO> roles = roleService.getAllRoles().stream()
                .map(role -> new RoleDTO(role.getId(), role.getName(), role.getDescription()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }
}
