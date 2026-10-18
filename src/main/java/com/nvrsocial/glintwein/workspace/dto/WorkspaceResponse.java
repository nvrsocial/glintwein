package com.nvrsocial.glintwein.workspace.dto;

import lombok.Getter;

import java.time.Instant;

@Getter

public class WorkspaceResponse {
    private final Long id;
    private final String name;
    private final String description;
    private final Instant createdAt;

    public WorkspaceResponse(Long id, String name, String description, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }
}
