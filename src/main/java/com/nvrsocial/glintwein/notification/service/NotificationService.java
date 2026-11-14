package com.nvrsocial.glintwein.notification.service;

import com.nvrsocial.glintwein.common.exception.NotificationNotFoundException;
import com.nvrsocial.glintwein.notification.component.NotificationMapper;
import com.nvrsocial.glintwein.notification.dto.NotificationResponse;
import com.nvrsocial.glintwein.notification.dto.UnreadCountResponse;
import com.nvrsocial.glintwein.notification.entity.Notification;
import com.nvrsocial.glintwein.notification.enums.NotificationType;
import com.nvrsocial.glintwein.notification.repository.NotificationRepository;
import com.nvrsocial.glintwein.task.entity.Task;
import com.nvrsocial.glintwein.user.entity.User;
import com.nvrsocial.glintwein.workspace.repository.WorkspaceMemberRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class NotificationService {
    private static final int MAX_PAGE_SIZE = 100;
    private static final int MAX_MESSAGE_LENGTH = 500;

    private final NotificationRepository notificationRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final NotificationMapper notificationMapper;

    public NotificationService(NotificationRepository notificationRepository, WorkspaceMemberRepository workspaceMemberRepository, NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.notificationMapper = notificationMapper;
    }

    @Transactional
    public void notifyUser(Long recipientId, User actor, NotificationType type, String message, Long workspaceId, Long projectId, Long taskId) {
        if (actor != null && actor.getId().equals(recipientId)) {
            return;
        }

        Long actorId = actor == null ? null : actor.getId();

        notificationRepository.save(new Notification(recipientId, actorId, type, truncate(message), workspaceId, projectId, taskId));
    }

    @Transactional
    public void notifyTaskParticipants(Task task, User actor, NotificationType type, String message) {
        Long projectId = task.getProject().getId();
        Long workspaceId = task.getProject().getWorkspace().getId();

        Set<Long> recipients = new LinkedHashSet<>();
        if (task.getCreatedBy() != null) {
            recipients.add(task.getCreatedBy().getId());
        }
        if (task.getAssignee() != null) {
            recipients.add(task.getAssignee().getId());
        }

        for (Long recipientId : recipients) {
            if (workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, recipientId)) {
                notifyUser(recipientId, actor, type, message, workspaceId, projectId, task.getId());
            }
        }
    }

    @Transactional
    public void deleteWorkspaceNotifications(Long workspaceId, Long recipientId) {
        notificationRepository.deleteByWorkspaceIdAndRecipientId(workspaceId, recipientId);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(Long currentUserId, boolean unreadOnly, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.clamp(size, 1, MAX_PAGE_SIZE), Sort.by(Sort.Direction.DESC, "createdAt", "id"));

        List<Notification> notifications = unreadOnly ? notificationRepository.findByRecipientIdAndReadFalse(currentUserId, pageable) : notificationRepository.findByRecipientId(currentUserId, pageable);

        return notifications.stream().map(notificationMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UnreadCountResponse getUnreadCount(Long currentUserId) {
        return new UnreadCountResponse(notificationRepository.countByRecipientIdAndReadFalse(currentUserId));
    }

    @Transactional
    public void markAsRead(Long notificationId, Long currentUserId) {
        Notification notification = notificationRepository.findByIdAndRecipientId(notificationId, currentUserId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found"));

        notification.setRead(true);
    }

    @Transactional
    public void markAllAsRead(Long currentUserId) {
        notificationRepository.markAllAsRead(currentUserId);
    }

    private String truncate(String message) {
        return message.length() <= MAX_MESSAGE_LENGTH ? message : message.substring(0, MAX_MESSAGE_LENGTH);
    }
}
