package com.nvrsocial.glintwein.comment.controller;

import com.nvrsocial.glintwein.comment.dto.CommentRequest;
import com.nvrsocial.glintwein.comment.dto.CommentResponse;
import com.nvrsocial.glintwein.comment.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/projects/{projectId}/tasks/{taskId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long workspaceId, @PathVariable Long projectId, @PathVariable Long taskId, @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CommentRequest request) {
        Long userId = Long.valueOf(jwt.getSubject());

        CommentResponse response = commentService.addComment(workspaceId, projectId, taskId, userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long workspaceId, @PathVariable Long projectId, @PathVariable Long taskId, @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(commentService.getComments(workspaceId, projectId, taskId, userId));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long workspaceId, @PathVariable Long projectId, @PathVariable Long taskId, @PathVariable Long commentId, @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CommentRequest request) {
        Long userId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(commentService.updateComment(workspaceId, projectId, taskId, commentId, userId, request));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long workspaceId, @PathVariable Long projectId, @PathVariable Long taskId, @PathVariable Long commentId, @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());

        commentService.deleteComment(workspaceId, projectId, taskId, commentId, userId);

        return ResponseEntity.noContent().build();
    }
}
