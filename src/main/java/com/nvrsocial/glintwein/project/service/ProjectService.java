package com.nvrsocial.glintwein.project.service;

import com.nvrsocial.glintwein.common.exception.UserNotFoundException;
import com.nvrsocial.glintwein.common.exception.WorkspaceMemberNotFoundException;
import com.nvrsocial.glintwein.common.exception.WorkspaceNotFoundException;
import com.nvrsocial.glintwein.project.component.ProjectMapper;
import com.nvrsocial.glintwein.project.dto.ProjectRequest;
import com.nvrsocial.glintwein.project.dto.ProjectResponse;
import com.nvrsocial.glintwein.project.entity.Project;
import com.nvrsocial.glintwein.project.repository.ProjectRepository;
import com.nvrsocial.glintwein.workspace.entity.Workspace;
import com.nvrsocial.glintwein.workspace.enums.WorkspaceRole;
import com.nvrsocial.glintwein.workspace.repository.WorkspaceRepository;
import com.nvrsocial.glintwein.workspace.service.WorkspaceAccessService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
