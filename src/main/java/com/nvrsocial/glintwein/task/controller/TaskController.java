package com.nvrsocial.glintwein.task.controller;

import com.nvrsocial.glintwein.task.dto.*;
import com.nvrsocial.glintwein.task.enums.TaskStatus;
import com.nvrsocial.glintwein.task.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/projects/{projectId}")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/tasks")
    public ResponseEntity<TaskResponse> createTask(@PathVariable Long workspaceId, @PathVariable Long projectId, @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody TaskRequest request) {
        Long userId = Long.valueOf(jwt.getSubject());

        TaskResponse response = taskService.createTask(workspaceId, projectId, userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskResponse>> getTasks(@PathVariable Long workspaceId, @PathVariable Long projectId, @RequestParam(required = false) TaskStatus status, @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(taskService.getTasks(workspaceId, projectId, userId, status));
    }

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long workspaceId, @PathVariable Long projectId, @PathVariable Long taskId, @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(taskService.getTaskById(workspaceId, projectId, taskId, userId));
    }

    @PatchMapping("/tasks/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long workspaceId, @PathVariable Long projectId, @PathVariable Long taskId, @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UpdateTaskRequest request) {
        Long userId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(taskService.updateTask(workspaceId, projectId, taskId, userId, request));
    }

    @PatchMapping("/tasks/{taskId}/move")
    public ResponseEntity<TaskResponse> moveTask(@PathVariable Long workspaceId, @PathVariable Long projectId, @PathVariable Long taskId, @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody MoveTaskRequest request) {
        Long userId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(taskService.moveTask(workspaceId, projectId, taskId, userId, request));
    }

    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long workspaceId, @PathVariable Long projectId, @PathVariable Long taskId, @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());

        taskService.deleteTask(workspaceId, projectId, taskId, userId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/board")
    public ResponseEntity<BoardResponse> getBoard(@PathVariable Long workspaceId, @PathVariable Long projectId, @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(taskService.getBoard(workspaceId, projectId, userId));
    }
}
