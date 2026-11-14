package com.nvrsocial.glintwein.task.component;

import com.nvrsocial.glintwein.task.dto.TaskResponse;
import com.nvrsocial.glintwein.task.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
    public TaskResponse toResponse(Task task) {
        Long createdById = task.getCreatedBy() == null ? null : task.getCreatedBy().getId();

        return new TaskResponse(task.getId(), task.getProject().getId(), task.getTitle(), task.getDescription(),
                task.getStatus(), task.getPosition(), createdById, task.getCreatedAt(), task.getUpdatedAt());
    }
}
