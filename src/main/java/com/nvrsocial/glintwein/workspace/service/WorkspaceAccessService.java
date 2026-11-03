package com.nvrsocial.glintwein.workspace.service;

import com.nvrsocial.glintwein.common.exception.WorkspaceAccessDeniedException;
import com.nvrsocial.glintwein.workspace.entity.WorkspaceMember;
import com.nvrsocial.glintwein.workspace.enums.WorkspaceRole;
import com.nvrsocial.glintwein.workspace.repository.WorkspaceMemberRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class WorkspaceAccessService {
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public WorkspaceAccessService(WorkspaceMemberRepository workspaceMemberRepository) {
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    public WorkspaceMember requireMember(Long workspaceId, Long userId) {
        return workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, userId)
                .orElseThrow(() -> new WorkspaceAccessDeniedException("You don't have access to this workspace"));
    }

    public WorkspaceMember requireRole(Long workspaceId, Long userId, WorkspaceRole... allowedRoles) {
        WorkspaceMember member = requireMember(workspaceId, userId);

        if (Arrays.stream(allowedRoles).noneMatch(role -> role == member.getRole())) {
            throw new WorkspaceAccessDeniedException("You don't have permission to perform this action");
        }

        return member;
    }
}
