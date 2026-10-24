package com.nvrsocial.glintwein.workspace.dto;

import com.nvrsocial.glintwein.workspace.enums.WorkspaceRole;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter

public class WorkspaceMemberResponse {
    private Long id;
    private String username;
    private String email;
    private WorkspaceRole role;
    private Instant joinedAt;

    public WorkspaceMemberResponse(Long id, String username, String email, WorkspaceRole role, Instant joinedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.joinedAt = joinedAt;
    }
}
