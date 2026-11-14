package com.nvrsocial.glintwein.notification.component;

import com.nvrsocial.glintwein.notification.dto.NotificationResponse;
import com.nvrsocial.glintwein.notification.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {
    public NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(notification.getId(), notification.getType(), notification.getMessage(),
                notification.isRead(), notification.getActorId(), notification.getWorkspaceId(),
                notification.getProjectId(), notification.getTaskId(), notification.getCreatedAt());
    }
}
