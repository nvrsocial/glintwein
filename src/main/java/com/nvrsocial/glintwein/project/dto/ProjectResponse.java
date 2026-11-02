package com.nvrsocial.glintwein.project.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter

public class ProjectResponse {
    private Long id;
    private Long workspaceId;
    private String name;
    private String description;
    private Instant createdAt;

    public ProjectResponse(Long id, Long workspaceId, String name, String description, Instant createdAt) {
        this.id = id;
        this.workspaceId = workspaceId;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }
}
