package com.nvrsocial.glintwein.workspace.service;

import com.nvrsocial.glintwein.common.exception.*;
import com.nvrsocial.glintwein.user.entity.User;
import com.nvrsocial.glintwein.user.repository.UserRepository;
import com.nvrsocial.glintwein.workspace.component.WorkspaceMapper;
import com.nvrsocial.glintwein.workspace.dto.UpdateWorkspaceRequest;
import com.nvrsocial.glintwein.workspace.dto.WorkspaceRequest;
import com.nvrsocial.glintwein.workspace.dto.WorkspaceResponse;
import com.nvrsocial.glintwein.workspace.entity.Workspace;
import com.nvrsocial.glintwein.workspace.entity.WorkspaceMember;
import com.nvrsocial.glintwein.workspace.enums.WorkspaceRole;
import com.nvrsocial.glintwein.workspace.repository.WorkspaceMemberRepository;
import com.nvrsocial.glintwein.workspace.repository.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WorkspaceService {
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UserRepository userRepository;
    private final WorkspaceMapper workspaceMapper;
    private final WorkspaceAccessService workspaceAccessService;


    public WorkspaceService(WorkspaceRepository workspaceRepository, WorkspaceMemberRepository workspaceMemberRepository, UserRepository userRepository, WorkspaceMapper workspaceMapper, WorkspaceAccessService workspaceAccessService) {
        this.workspaceRepository = workspaceRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.userRepository = userRepository;
        this.workspaceMapper = workspaceMapper;
        this.workspaceAccessService = workspaceAccessService;
    }

    @Transactional
    public WorkspaceResponse createWorkspace(WorkspaceRequest request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        Workspace workspace = new Workspace();
        workspace.setName(request.getName());
        workspace.setDescription(request.getDescription());

        Workspace savedWorkspace = workspaceRepository.save(workspace);

        WorkspaceMember workspaceMember = new WorkspaceMember();
        workspaceMember.setUser(user);
        workspaceMember.setWorkspace(savedWorkspace);
        workspaceMember.setRole(WorkspaceRole.OWNER);

        workspaceMemberRepository.save(workspaceMember);

        return workspaceMapper.toResponse(savedWorkspace);
    }

    @Transactional(readOnly = true)
    public List<WorkspaceResponse> getMyWorkspaces(Long userId) {
        List<WorkspaceMember> workspaceMemberList = workspaceMemberRepository.findByUserId(userId);
        List<Workspace> workspaceList = workspaceMemberList.stream().map(WorkspaceMember::getWorkspace).toList();

        return workspaceList.stream().map(workspaceMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public WorkspaceResponse getWorkspaceById(Long workspaceId, Long currentUserId) {
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));

        workspaceAccessService.requireMember(workspaceId, currentUserId);

        return workspaceMapper.toResponse(workspace);
    }

    @Transactional
    public WorkspaceResponse updateWorkspace(Long workspaceId, Long currentUserId, UpdateWorkspaceRequest updateWorkspaceRequest) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));

        workspaceAccessService.requireRole(workspaceId, currentUserId, WorkspaceRole.OWNER, WorkspaceRole.ADMIN);

        if (updateWorkspaceRequest.getName() == null && updateWorkspaceRequest.getDescription() == null) {
            throw new InvalidWorkspaceRequestException("Name and description is null");
        }

        if (updateWorkspaceRequest.getName() != null) {
            String name = updateWorkspaceRequest.getName().trim();

            if (name.length() < 3 || name.length() > 100) {
                throw new InvalidWorkspaceRequestException("Workspace name must contain between 3 and 100 characters");
            }

            workspace.setName(name);
        }

        if (updateWorkspaceRequest.getDescription() != null) {
            workspace.setDescription(updateWorkspaceRequest.getDescription());
        }

        workspaceRepository.save(workspace);

        return workspaceMapper.toResponse(workspace);
    }

    @Transactional
    public void deleteWorkspace(Long workspaceId, Long currentUserId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));

        workspaceAccessService.requireRole(workspaceId, currentUserId, WorkspaceRole.OWNER);

        List<WorkspaceMember> workspaceMemberList = workspaceMemberRepository.findByWorkspaceId(workspaceId);

        workspaceMemberRepository.deleteAll(workspaceMemberList);
        workspaceRepository.delete(workspace);
    }

}
