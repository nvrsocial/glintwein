package com.nvrsocial.glintwein.task.repository;

import com.nvrsocial.glintwein.task.entity.Task;
import com.nvrsocial.glintwein.task.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    List<Task> findByProjectIdOrderByPositionAsc(Long projectId);

    List<Task> findByProjectIdAndStatusOrderByPositionAsc(Long projectId, TaskStatus status);

    List<Task> findByAssigneeIdAndProjectWorkspaceId(Long assigneeId, Long workspaceId);

    Optional<Task> findByIdAndProjectId(Long id, Long projectId);

    long countByProjectIdAndStatus(Long projectId, TaskStatus status);
}
