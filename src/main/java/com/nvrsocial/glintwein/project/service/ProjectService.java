package com.nvrsocial.glintwein.project.service;

import com.nvrsocial.glintwein.common.exception.InvalidWorkspaceRequestException;
import com.nvrsocial.glintwein.common.exception.ProjectNotFoundException;
import com.nvrsocial.glintwein.common.exception.WorkspaceNotFoundException;
import com.nvrsocial.glintwein.project.component.ProjectMapper;
import com.nvrsocial.glintwein.project.dto.ProjectRequest;
import com.nvrsocial.glintwein.project.dto.ProjectResponse;
import com.nvrsocial.glintwein.project.dto.UpdateProjectRequest;
import com.nvrsocial.glintwein.project.entity.Project;
import com.nvrsocial.glintwein.project.repository.ProjectRepository;
import com.nvrsocial.glintwein.workspace.entity.Workspace;
import com.nvrsocial.glintwein.workspace.enums.WorkspaceRole;
import com.nvrsocial.glintwein.workspace.repository.WorkspaceRepository;
import com.nvrsocial.glintwein.workspace.service.WorkspaceAccessService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceAccessService workspaceAccessService;
    private final ProjectMapper projectMapper;

    public ProjectService(ProjectRepository projectRepository, WorkspaceRepository workspaceRepository, WorkspaceAccessService workspaceAccessService, ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.workspaceRepository = workspaceRepository;
        this.workspaceAccessService = workspaceAccessService;
        this.projectMapper = projectMapper;
    }


    @Transactional
    public ProjectResponse createProject(Long workspaceId, Long currentUserId, ProjectRequest projectRequest) {

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));

        workspaceAccessService.requireRole(workspaceId, currentUserId, WorkspaceRole.OWNER, WorkspaceRole.ADMIN);

        Project project = new Project();
        project.setWorkspace(workspace);
        project.setName(projectRequest.getName().trim());
        project.setDescription(projectRequest.getDescription());

        Project savedProject = projectRepository.save(project);

        return projectMapper.toResponse(savedProject);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjects(Long workspaceId, Long currentUserId) {
        workspaceAccessService.requireMember(workspaceId, currentUserId);

        return projectRepository.findByWorkspaceIdOrderByCreatedAtAsc(workspaceId).stream()
                .map(projectMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long workspaceId, Long projectId, Long currentUserId) {
        workspaceAccessService.requireMember(workspaceId, currentUserId);

        return projectMapper.toResponse(findProject(workspaceId, projectId));
    }

    @Transactional
    public ProjectResponse updateProject(Long workspaceId, Long projectId, Long currentUserId, UpdateProjectRequest request) {
        workspaceAccessService.requireRole(workspaceId, currentUserId, WorkspaceRole.OWNER, WorkspaceRole.ADMIN);

        Project project = findProject(workspaceId, projectId);

        if (request.getName() == null && request.getDescription() == null) {
            throw new InvalidWorkspaceRequestException("Name or description must be provided");
        }

        if (request.getName() != null) {
            String name = request.getName().trim();

            if (name.length() < 3 || name.length() > 100) {
                throw new InvalidWorkspaceRequestException("Project name must contain between 3 and 100 characters");
            }

            project.setName(name);
        }

        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }

        Project savedProject = projectRepository.saveAndFlush(project);

        return projectMapper.toResponse(savedProject);
    }

    @Transactional
    public void deleteProject(Long workspaceId, Long projectId, Long currentUserId) {
        workspaceAccessService.requireRole(workspaceId, currentUserId, WorkspaceRole.OWNER, WorkspaceRole.ADMIN);

        projectRepository.delete(findProject(workspaceId, projectId));
    }

    private Project findProject(Long workspaceId, Long projectId) {
        return projectRepository.findByIdAndWorkspaceId(projectId, workspaceId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));
    }
}
