package com.nvrsocial.glintwein.activity.dto;

import com.nvrsocial.glintwein.activity.enums.TaskActivityType;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter

public class TaskActivityResponse {
    private Long id;
    private Long taskId;
    private Long actorId;
    private String actorUsername;
    private TaskActivityType type;
    private String oldValue;
    private String newValue;
    private Instant createdAt;

    public TaskActivityResponse(Long id, Long taskId, Long actorId, String actorUsername, TaskActivityType type,
                                String oldValue, String newValue, Instant createdAt) {
        this.id = id;
        this.taskId = taskId;
        this.actorId = actorId;
        this.actorUsername = actorUsername;
        this.type = type;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.createdAt = createdAt;
    }
}
