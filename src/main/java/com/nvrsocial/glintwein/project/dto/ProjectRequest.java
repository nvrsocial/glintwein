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

    @Size(max = 1000)
    private String description;

    public ProjectRequest() {
    }

    public ProjectRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
