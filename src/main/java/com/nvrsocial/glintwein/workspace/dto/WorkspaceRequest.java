package com.nvrsocial.glintwein.workspace.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class WorkspaceRequest {
    @Size(min = 3, max = 100)
    private String name;

    @Size(max = 1000)
    private String description;

    public WorkspaceRequest() {}

    public WorkspaceRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
