package com.nvrsocial.glintwein.activity.service;

import com.nvrsocial.glintwein.activity.component.TaskActivityMapper;
import com.nvrsocial.glintwein.activity.dto.TaskActivityResponse;
import com.nvrsocial.glintwein.activity.entity.TaskActivity;
import com.nvrsocial.glintwein.activity.enums.TaskActivityType;
import com.nvrsocial.glintwein.activity.repository.TaskActivityRepository;
import com.nvrsocial.glintwein.task.entity.Task;
import com.nvrsocial.glintwein.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskActivityService {
    private static final int MAX_VALUE_LENGTH = 255;

    private final TaskActivityRepository taskActivityRepository;
    private final TaskActivityMapper taskActivityMapper;

    public TaskActivityService(TaskActivityRepository taskActivityRepository, TaskActivityMapper taskActivityMapper) {
        this.taskActivityRepository = taskActivityRepository;
        this.taskActivityMapper = taskActivityMapper;
    }

    @Transactional
    public void record(Task task, User actor, TaskActivityType type) {
        record(task, actor, type, null, null);
    }

    @Transactional
    public void record(Task task, User actor, TaskActivityType type, String oldValue, String newValue) {
        taskActivityRepository.save(new TaskActivity(task, actor, type, truncate(oldValue), truncate(newValue)));
    }

    @Transactional(readOnly = true)
    public List<TaskActivityResponse> getTaskActivity(Long taskId) {
        return taskActivityRepository.findByTaskIdOrderByCreatedAtAscIdAsc(taskId).stream().map(taskActivityMapper::toResponse).toList();
    }

    private String truncate(String value) {
        if (value == null || value.length() <= MAX_VALUE_LENGTH) {
            return value;
        }

        return value.substring(0, MAX_VALUE_LENGTH);
    }
}
