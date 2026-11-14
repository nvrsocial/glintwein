package com.nvrsocial.glintwein.task.repository;

import com.nvrsocial.glintwein.task.entity.Task;
import com.nvrsocial.glintwein.task.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectIdOrderByCreatedAtAsc(Long projectId);

    List<Task> findByProjectIdOrderByPositionAsc(Long projectId);

    List<Task> findByProjectIdAndStatusOrderByPositionAsc(Long projectId, TaskStatus status);

    Optional<Task> findByIdAndProjectId(Long id, Long projectId);

    long countByProjectIdAndStatus(Long projectId, TaskStatus status);
}
