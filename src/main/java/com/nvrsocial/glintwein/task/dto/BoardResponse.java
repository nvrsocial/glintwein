package com.nvrsocial.glintwein.task.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter

public class BoardResponse {
    private Long projectId;
    private List<BoardColumnResponse> columns;

    public BoardResponse(Long projectId, List<BoardColumnResponse> columns) {
        this.projectId = projectId;
        this.columns = columns;
    }
}
