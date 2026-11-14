package com.nvrsocial.glintwein.task.repository;

import com.nvrsocial.glintwein.task.entity.Task;
import com.nvrsocial.glintwein.task.enums.TaskPriority;
import com.nvrsocial.glintwein.task.enums.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

public final class TaskSpecifications {
    private TaskSpecifications() {}

    public static Specification<Task> inProject(Long projectId) {
        return (root, query, cb) -> cb.equal(root.get("project").get("id"), projectId);
    }

    public static Specification<Task> hasStatus(TaskStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Task> hasPriority(TaskPriority priority) {
        return (root, query, cb) -> cb.equal(root.get("priority"), priority);
    }

    public static Specification<Task> hasAssignee(Long assigneeId) {
        return (root, query, cb) -> cb.equal(root.get("assignee").get("id"), assigneeId);
    }
}
