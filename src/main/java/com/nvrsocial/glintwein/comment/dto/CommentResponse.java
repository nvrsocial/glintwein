package com.nvrsocial.glintwein.comment.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter

public class CommentResponse {
    private Long id;
    private Long taskId;
    private Long authorId;
    private String authorUsername;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;

    public CommentResponse(Long id, Long taskId, Long authorId, String authorUsername, String content,
                           Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.taskId = taskId;
        this.authorId = authorId;
        this.authorUsername = authorUsername;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
