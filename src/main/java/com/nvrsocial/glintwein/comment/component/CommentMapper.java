package com.nvrsocial.glintwein.comment.component;

import com.nvrsocial.glintwein.comment.dto.CommentResponse;
import com.nvrsocial.glintwein.comment.entity.TaskComment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public CommentResponse toResponse(TaskComment comment) {
        Long authorId = comment.getAuthor() == null ? null : comment.getAuthor().getId();
        String authorUsername = comment.getAuthor() == null ? null : comment.getAuthor().getUsername();

        return new CommentResponse(comment.getId(), comment.getTask().getId(), authorId, authorUsername,
                comment.getContent(), comment.getCreatedAt(), comment.getUpdatedAt());
    }
}
