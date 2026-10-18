package com.nvrsocial.glintwein.workspace.repository;

import com.nvrsocial.glintwein.user.entity.User;
import com.nvrsocial.glintwein.workspace.entity.WorkspaceMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, Long> {

    boolean existsByWorkspaceIdAndUserId(Long workspaceId, Long userId);

    List<WorkspaceMember> findByUserId(Long userId);

    List<WorkspaceMember> findByWorkspaceId(Long workspaceId);

    Long user(User user);
}
