package com.nvrsocial.glintwein.workspace.service;

import com.nvrsocial.glintwein.common.exception.UserNotFoundException;
import com.nvrsocial.glintwein.common.exception.WorkspaceAccessDeniedException;
import com.nvrsocial.glintwein.common.exception.WorkspaceMemberAlreadyExistsException;
import com.nvrsocial.glintwein.common.exception.WorkspaceNotFoundException;
import com.nvrsocial.glintwein.user.entity.User;
import com.nvrsocial.glintwein.user.repository.UserRepository;
import com.nvrsocial.glintwein.workspace.component.WorkspaceMemberMapper;
import com.nvrsocial.glintwein.workspace.dto.WorkspaceMemberRequest;
import com.nvrsocial.glintwein.workspace.dto.WorkspaceMemberResponse;
import com.nvrsocial.glintwein.workspace.entity.Workspace;
import com.nvrsocial.glintwein.workspace.entity.WorkspaceMember;
import com.nvrsocial.glintwein.workspace.enums.WorkspaceRole;
import com.nvrsocial.glintwein.workspace.repository.WorkspaceMemberRepository;
import com.nvrsocial.glintwein.workspace.repository.WorkspaceRepository;
import org.hibernate.jdbc.Work;
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

    public WorkspaceMemberService(WorkspaceMemberRepository workspaceMemberRepository, WorkspaceMemberMapper WorkspaceMemberMapper,
                                  WorkspaceRepository workspaceRepository, UserRepository userRepository) {
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
}
