package com.nvrsocial.glintwein.notification.entity;

import com.nvrsocial.glintwein.notification.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;

    @Column(name = "actor_id")
    private Long actorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private NotificationType type;

    @Column(name = "message", nullable = false, length = 500)
    private String message;

    @Column(name = "workspace_id", nullable = false)
    private Long workspaceId;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "task_id")
    private Long taskId;

    @Column(name = "is_read", nullable = false)
    private boolean read;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public Notification() {}

    public Notification(Long recipientId, Long actorId, NotificationType type, String message,
                        Long workspaceId, Long projectId, Long taskId) {
        this.recipientId = recipientId;
        this.actorId = actorId;
        this.type = type;
        this.message = message;
        this.workspaceId = workspaceId;
        this.projectId = projectId;
        this.taskId = taskId;
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
