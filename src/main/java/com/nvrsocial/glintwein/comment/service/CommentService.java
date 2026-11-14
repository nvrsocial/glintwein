package com.nvrsocial.glintwein.comment.service;

import com.nvrsocial.glintwein.activity.enums.TaskActivityType;
import com.nvrsocial.glintwein.activity.service.TaskActivityService;
import com.nvrsocial.glintwein.comment.component.CommentMapper;
import com.nvrsocial.glintwein.comment.dto.CommentRequest;
import com.nvrsocial.glintwein.comment.dto.CommentResponse;
import com.nvrsocial.glintwein.comment.entity.TaskComment;
import com.nvrsocial.glintwein.comment.repository.TaskCommentRepository;
import com.nvrsocial.glintwein.common.exception.CommentNotFoundException;
import com.nvrsocial.glintwein.common.exception.WorkspaceAccessDeniedException;
import com.nvrsocial.glintwein.notification.enums.NotificationType;
import com.nvrsocial.glintwein.notification.service.NotificationService;
import com.nvrsocial.glintwein.task.entity.Task;
import com.nvrsocial.glintwein.task.service.TaskService;
import com.nvrsocial.glintwein.user.entity.User;
import com.nvrsocial.glintwein.workspace.entity.WorkspaceMember;
import com.nvrsocial.glintwein.workspace.enums.WorkspaceRole;
import com.nvrsocial.glintwein.workspace.service.WorkspaceAccessService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {
    private final TaskCommentRepository taskCommentRepository;
    private final TaskService taskService;
    private final WorkspaceAccessService workspaceAccessService;
    private final TaskActivityService taskActivityService;
    private final NotificationService notificationService;
    private final CommentMapper commentMapper;

    public CommentService(TaskCommentRepository taskCommentRepository, TaskService taskService, WorkspaceAccessService workspaceAccessService,
                          TaskActivityService taskActivityService, NotificationService notificationService, CommentMapper commentMapper) {
        this.taskCommentRepository = taskCommentRepository;
        this.taskService = taskService;
        this.workspaceAccessService = workspaceAccessService;
        this.taskActivityService = taskActivityService;
        this.notificationService = notificationService;
        this.commentMapper = commentMapper;
    }

    @Transactional
    public CommentResponse addComment(Long workspaceId, Long projectId, Long taskId, Long currentUserId, CommentRequest request) {
        WorkspaceMember member = workspaceAccessService.requireMember(workspaceId, currentUserId);

        Task task = taskService.requireTask(workspaceId, projectId, taskId);
        User author = member.getUser();

        TaskComment comment = taskCommentRepository.save(new TaskComment(task, author, request.getContent().trim()));

        taskActivityService.record(task, author, TaskActivityType.COMMENT_ADDED);
        notificationService.notifyTaskParticipants(task, author, NotificationType.TASK_COMMENTED,
                author.getUsername() + " commented on task \"" + task.getTitle() + "\"");

        return commentMapper.toResponse(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long workspaceId, Long projectId, Long taskId, Long currentUserId) {
        workspaceAccessService.requireMember(workspaceId, currentUserId);

        taskService.requireTask(workspaceId, projectId, taskId);

        return taskCommentRepository.findByTaskIdOrderByCreatedAtAscIdAsc(taskId).stream()
                .map(commentMapper::toResponse)
                .toList();
    }

    @Transactional
    public CommentResponse updateComment(Long workspaceId, Long projectId, Long taskId, Long commentId, Long currentUserId, CommentRequest request) {
        workspaceAccessService.requireMember(workspaceId, currentUserId);

        taskService.requireTask(workspaceId, projectId, taskId);
        TaskComment comment = findComment(taskId, commentId);

        if (!isAuthor(comment, currentUserId)) {
            throw new WorkspaceAccessDeniedException("You can only edit your own comments");
        }

        comment.setContent(request.getContent().trim());

        return commentMapper.toResponse(taskCommentRepository.saveAndFlush(comment));
    }

    @Transactional
    public void deleteComment(Long workspaceId, Long projectId, Long taskId, Long commentId, Long currentUserId) {
        WorkspaceMember member = workspaceAccessService.requireMember(workspaceId, currentUserId);

        taskService.requireTask(workspaceId, projectId, taskId);
        TaskComment comment = findComment(taskId, commentId);

        boolean isManager = member.getRole() == WorkspaceRole.OWNER || member.getRole() == WorkspaceRole.ADMIN;

        if (!isManager && !isAuthor(comment, currentUserId)) {
            throw new WorkspaceAccessDeniedException("You don't have permission to perform this action");
        }

        taskCommentRepository.delete(comment);
    }

    private boolean isAuthor(TaskComment comment, Long userId) {
        return comment.getAuthor() != null && comment.getAuthor().getId().equals(userId);
    }

    private TaskComment findComment(Long taskId, Long commentId) {
        return taskCommentRepository.findByIdAndTaskId(commentId, taskId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));
    }
}
