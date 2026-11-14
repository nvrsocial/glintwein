package com.nvrsocial.glintwein.task.dto;

import com.nvrsocial.glintwein.task.enums.TaskStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter

public class TaskResponse {
    private Long id;
    private Long projectId;
    private String title;
    private String description;
    private TaskStatus status;
    private Integer position;
    private Long createdById;
    private Instant createdAt;
    private Instant updatedAt;

    public TaskResponse(Long id, Long projectId, String title, String description, TaskStatus status,
                        Integer position, Long createdById, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.position = position;
        this.createdById = createdById;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
