package com.nvrsocial.glintwein.task.dto;

import com.nvrsocial.glintwein.task.enums.TaskPriority;
import com.nvrsocial.glintwein.task.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class TaskRequest {
    @NotBlank
    @Size(max = 200)
    private String title;

    @Size(max = 5000)
    private String description;

    private TaskStatus status;

    private TaskPriority priority;

    private Long assigneeId;

    public TaskRequest() {
    }

    public TaskRequest(String title, String description, TaskStatus status, TaskPriority priority, Long assigneeId) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.assigneeId = assigneeId;
    }
}
