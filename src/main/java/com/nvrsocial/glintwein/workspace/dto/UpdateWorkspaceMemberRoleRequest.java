package com.nvrsocial.glintwein.workspace.dto;

import com.nvrsocial.glintwein.workspace.enums.WorkspaceRole;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UpdateWorkspaceMemberRoleRequest {
    @NotNull
    private WorkspaceRole role;

    public UpdateWorkspaceMemberRoleRequest() {}

    public UpdateWorkspaceMemberRoleRequest(WorkspaceRole role) {
        this.role = role;
    }
}
