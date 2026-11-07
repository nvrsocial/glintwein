package com.nvrsocial.glintwein.project.controller;

import com.nvrsocial.glintwein.project.dto.ProjectRequest;
import com.nvrsocial.glintwein.project.dto.ProjectResponse;
import com.nvrsocial.glintwein.project.dto.UpdateProjectRequest;
import com.nvrsocial.glintwein.project.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@PathVariable Long workspaceId, @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ProjectRequest projectRequest) {
        Long userId = Long.valueOf(jwt.getSubject());

        ProjectResponse response = projectService.createProject(workspaceId, userId, projectRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getProjects(@PathVariable Long workspaceId, @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(projectService.getProjects(workspaceId, userId));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long workspaceId, @PathVariable Long projectId, @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(projectService.getProjectById(workspaceId, projectId, userId));
    }

    @PatchMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long workspaceId, @PathVariable Long projectId, @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UpdateProjectRequest request) {
        Long userId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(projectService.updateProject(workspaceId, projectId, userId, request));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long workspaceId, @PathVariable Long projectId, @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());

        projectService.deleteProject(workspaceId, projectId, userId);

        return ResponseEntity.noContent().build();
    }
}
