package com.nvrsocial.glintwein.activity.entity;

import com.nvrsocial.glintwein.activity.enums.TaskActivityType;
import com.nvrsocial.glintwein.task.entity.Task;
import com.nvrsocial.glintwein.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter

@Entity
@Table(name = "task_activities")
public class TaskActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private User actor;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private TaskActivityType type;

    @Column(name = "old_value")
    private String oldValue;

    @Column(name = "new_value")
    private String newValue;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public TaskActivity() {}

    public TaskActivity(Task task, User actor, TaskActivityType type, String oldValue, String newValue) {
        this.task = task;
        this.actor = actor;
        this.type = type;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
