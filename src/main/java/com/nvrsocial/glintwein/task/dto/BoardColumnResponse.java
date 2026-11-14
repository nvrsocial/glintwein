package com.nvrsocial.glintwein.task.dto;

import com.nvrsocial.glintwein.task.enums.TaskStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter

public class BoardColumnResponse {
    private TaskStatus status;
    private List<TaskResponse> tasks;

    public BoardColumnResponse(TaskStatus status, List<TaskResponse> tasks) {
        this.status = status;
        this.tasks = tasks;
    }
}
