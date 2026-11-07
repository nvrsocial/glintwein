package com.nvrsocial.glintwein.project.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProjectRequest {
    @Size(max = 100)
    private String name;

    @Size(max = 1000)
    private String description;

    public UpdateProjectRequest() {}

    public UpdateProjectRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
