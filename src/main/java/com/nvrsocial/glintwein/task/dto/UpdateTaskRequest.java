package com.nvrsocial.glintwein.task.dto;

import com.nvrsocial.glintwein.task.enums.TaskPriority;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTaskRequest {
    @Size(max = 200)
    private String title;

    @Size(max = 5000)
    private String description;

    private TaskPriority priority;

    public UpdateTaskRequest() {}

    public UpdateTaskRequest(String title, String description, TaskPriority priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
    }
}
