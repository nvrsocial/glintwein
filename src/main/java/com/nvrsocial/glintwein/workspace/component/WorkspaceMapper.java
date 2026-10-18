package com.nvrsocial.glintwein.workspace.component;

import com.nvrsocial.glintwein.workspace.dto.WorkspaceResponse;
import com.nvrsocial.glintwein.workspace.entity.Workspace;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceMapper {
    public WorkspaceResponse toResponse(Workspace workspace) {
        return new WorkspaceResponse(workspace.getId(), workspace.getName(), workspace.getDescription(), workspace.getCreatedAt());
    }
}
