package com.nvrsocial.glintwein.notification.dto;

import com.nvrsocial.glintwein.notification.enums.NotificationType;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter

public class NotificationResponse {
    private Long id;
    private NotificationType type;
    private String message;
    private boolean read;
    private Long actorId;
    private Long workspaceId;
    private Long projectId;
    private Long taskId;
    private Instant createdAt;

    public NotificationResponse(Long id, NotificationType type, String message, boolean read, Long actorId,
                                Long workspaceId, Long projectId, Long taskId, Instant createdAt) {
        this.id = id;
        this.type = type;
        this.message = message;
        this.read = read;
        this.actorId = actorId;
        this.workspaceId = workspaceId;
        this.projectId = projectId;
        this.taskId = taskId;
        this.createdAt = createdAt;
    }
}
