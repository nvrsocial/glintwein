package com.nvrsocial.glintwein.workspace.service;

import com.nvrsocial.glintwein.common.exception.UserNotFoundException;
import com.nvrsocial.glintwein.user.entity.User;
import com.nvrsocial.glintwein.user.repository.UserRepository;
import com.nvrsocial.glintwein.workspace.component.WorkspaceMapper;
import com.nvrsocial.glintwein.workspace.dto.WorkspaceRequest;
import com.nvrsocial.glintwein.workspace.dto.WorkspaceResponse;
import com.nvrsocial.glintwein.workspace.entity.Workspace;
import com.nvrsocial.glintwein.workspace.entity.WorkspaceMember;
import com.nvrsocial.glintwein.workspace.enums.WorkspaceRole;
import com.nvrsocial.glintwein.workspace.repository.WorkspaceMemberRepository;
import com.nvrsocial.glintwein.workspace.repository.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class WorkspaceService {
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UserRepository userRepository;
    private final WorkspaceMapper workspaceMapper;


    public WorkspaceService(WorkspaceRepository workspaceRepository, WorkspaceMemberRepository workspaceMemberRepository, UserRepository userRepository, WorkspaceMapper workspaceMapper) {
        this.workspaceRepository = workspaceRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.userRepository = userRepository;
        this.workspaceMapper = workspaceMapper;
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
}
