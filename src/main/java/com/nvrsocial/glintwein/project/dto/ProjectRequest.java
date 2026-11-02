package com.nvrsocial.glintwein.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ProjectRequest {
    @NotBlank
    @Size(min = 3, max = 100)
    private String name;
    private String description;

    public ProjectRequest() {
    }

    public ProjectRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
