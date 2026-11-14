package com.nvrsocial.glintwein.task.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignTaskRequest {
    private Long assigneeId;

    public AssignTaskRequest() {}

    public AssignTaskRequest(Long assigneeId) {
        this.assigneeId = assigneeId;
    }
}
