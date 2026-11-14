package com.nvrsocial.glintwein.task.dto;

import com.nvrsocial.glintwein.task.enums.TaskStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveTaskRequest {
    @NotNull
    private TaskStatus status;

    @Min(0)
    private Integer position;

    public MoveTaskRequest() {}

    public MoveTaskRequest(TaskStatus status, Integer position) {
        this.status = status;
        this.position = position;
    }
}
