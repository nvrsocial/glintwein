package com.nvrsocial.glintwein.workspace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateWorkspaceRequest {
    @Size(max = 100)
    private String name;

    private String description;

    public UpdateWorkspaceRequest() {}

    public UpdateWorkspaceRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
