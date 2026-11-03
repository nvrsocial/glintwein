package com.nvrsocial.glintwein.workspace.service;

import com.nvrsocial.glintwein.common.exception.*;
import com.nvrsocial.glintwein.user.entity.User;
import com.nvrsocial.glintwein.user.repository.UserRepository;
import com.nvrsocial.glintwein.workspace.component.WorkspaceMemberMapper;
import com.nvrsocial.glintwein.workspace.dto.UpdateWorkspaceMemberRoleRequest;
import com.nvrsocial.glintwein.workspace.dto.WorkspaceMemberRequest;
import com.nvrsocial.glintwein.workspace.dto.WorkspaceMemberResponse;
import com.nvrsocial.glintwein.workspace.entity.Workspace;
import com.nvrsocial.glintwein.workspace.entity.WorkspaceMember;
import com.nvrsocial.glintwein.workspace.enums.WorkspaceRole;
import com.nvrsocial.glintwein.workspace.repository.WorkspaceMemberRepository;
import com.nvrsocial.glintwein.workspace.repository.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WorkspaceMemberService {
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceMemberMapper workspaceMemberMapper;
    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;

    public WorkspaceMemberService(WorkspaceMemberRepository workspaceMemberRepository, WorkspaceMemberMapper WorkspaceMemberMapper, WorkspaceRepository workspaceRepository, UserRepository userRepository) {
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.workspaceMemberMapper = WorkspaceMemberMapper;
        this.workspaceRepository = workspaceRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<WorkspaceMemberResponse> getWorkspaceMembers(Long workspaceId, Long userId) {
        if (!workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, userId)) {
            throw new WorkspaceAccessDeniedException("You don't have access to this workspace");
        }

        List<WorkspaceMember> workspaceMembers = workspaceMemberRepository.findByWorkspaceId(workspaceId);

        return workspaceMembers.stream().map(workspaceMemberMapper::toResponse).toList();
    }

    @Transactional
    public WorkspaceMemberResponse addMember(Long workspaceId, Long currentUserId, WorkspaceMemberRequest request) {
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));
        WorkspaceMember currentMember = workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, currentUserId).orElseThrow(() -> new WorkspaceAccessDeniedException("You don't have access to this workspace"));

        if (currentMember.getRole() != WorkspaceRole.OWNER && currentMember.getRole() != WorkspaceRole.ADMIN) {
            throw new WorkspaceAccessDeniedException("You don't have permission to add members");
        }

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, user.getId())) {
            throw new WorkspaceMemberAlreadyExistsException("User is already a member of this workspace");
        }

        WorkspaceMember member = new WorkspaceMember();
        member.setUser(user);
        member.setWorkspace(workspace);
        member.setRole(WorkspaceRole.MEMBER);

        WorkspaceMember savedMember = workspaceMemberRepository.save(member);

        return workspaceMemberMapper.toResponse(savedMember);
    }


    @Transactional
    public WorkspaceMemberResponse updateMemberRole(Long workspaceId, Long currentUserId, Long targetUserId, UpdateWorkspaceMemberRoleRequest request) {

        if (!workspaceRepository.existsById(workspaceId)) {
            throw new WorkspaceNotFoundException("Workspace not found");
        }

        WorkspaceMember currentMember = workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, currentUserId).orElseThrow(() -> new WorkspaceAccessDeniedException("You don't have access to this workspace"));

        if (currentMember.getRole() != WorkspaceRole.OWNER) {
            throw new WorkspaceAccessDeniedException("Only workspace owner can change member roles");
        }

        if (currentUserId.equals(targetUserId)) {
            throw new WorkspaceAccessDeniedException("You cannot change your own role");
        }

        WorkspaceMember targetMember = workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, targetUserId).orElseThrow(() -> new WorkspaceMemberNotFoundException("Workspace member not found"));

        if (targetMember.getRole() == WorkspaceRole.OWNER || request.getRole() == WorkspaceRole.OWNER) {
            throw new WorkspaceAccessDeniedException("Owner role cannot be changed through this endpoint");
        }

        targetMember.setRole(request.getRole());

        return workspaceMemberMapper.toResponse(targetMember);
    }


    @Transactional
    public void removeMember(Long workspaceId, Long currentUserId, Long targetUserId) {
        if (!workspaceRepository.existsById(workspaceId)) {
            throw new WorkspaceNotFoundException("Workspace not found");
        }

        WorkspaceMember currentUser = workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, currentUserId).orElseThrow(() -> new WorkspaceAccessDeniedException("You don't have access to this workspace"));

        if (currentUser.getRole() != WorkspaceRole.OWNER && currentUser.getRole() != WorkspaceRole.ADMIN) {
            throw new WorkspaceAccessDeniedException("You don't have permission to remove members");
        }

        if (currentUserId.equals(targetUserId)) {
            throw new WorkspaceAccessDeniedException("You cannot remove yourself from the workspace");
        }

        WorkspaceMember targetUser = workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, targetUserId).orElseThrow(() -> new WorkspaceMemberNotFoundException("Workspace member not found"));

        if (targetUser.getRole() == WorkspaceRole.OWNER) {
            throw new WorkspaceAccessDeniedException("Workspace owner cannot be removed");
        }

        if (currentUser.getRole() == WorkspaceRole.ADMIN && targetUser.getRole() != WorkspaceRole.MEMBER) {
            throw new WorkspaceAccessDeniedException("Admin can only remove members");
        }

        workspaceMemberRepository.delete(targetUser);
    }

}

