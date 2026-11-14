package com.nvrsocial.glintwein.activity.repository;

import com.nvrsocial.glintwein.activity.entity.TaskActivity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskActivityRepository extends JpaRepository<TaskActivity, Long> {
    @EntityGraph(attributePaths = "actor")
    List<TaskActivity> findByTaskIdOrderByCreatedAtAscIdAsc(Long taskId);
}
