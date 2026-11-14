package com.nvrsocial.glintwein.task.service;

import com.nvrsocial.glintwein.common.exception.InvalidWorkspaceRequestException;
import com.nvrsocial.glintwein.common.exception.ProjectNotFoundException;
import com.nvrsocial.glintwein.common.exception.TaskNotFoundException;
import com.nvrsocial.glintwein.common.exception.WorkspaceAccessDeniedException;
import com.nvrsocial.glintwein.project.entity.Project;
import com.nvrsocial.glintwein.project.repository.ProjectRepository;
import com.nvrsocial.glintwein.task.component.TaskMapper;
import com.nvrsocial.glintwein.task.dto.*;
import com.nvrsocial.glintwein.task.entity.Task;
import com.nvrsocial.glintwein.task.enums.TaskStatus;
import com.nvrsocial.glintwein.task.repository.TaskRepository;
import com.nvrsocial.glintwein.user.repository.UserRepository;
import com.nvrsocial.glintwein.workspace.entity.WorkspaceMember;
import com.nvrsocial.glintwein.workspace.enums.WorkspaceRole;
import com.nvrsocial.glintwein.workspace.service.WorkspaceAccessService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final WorkspaceAccessService workspaceAccessService;
    private final TaskMapper taskMapper;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository, UserRepository userRepository,
                       WorkspaceAccessService workspaceAccessService, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.workspaceAccessService = workspaceAccessService;
        this.taskMapper = taskMapper;
    }

    @Transactional
    public TaskResponse createTask(Long workspaceId, Long projectId, Long currentUserId, TaskRequest request) {
        workspaceAccessService.requireMember(workspaceId, currentUserId);

        Project project = lockProject(workspaceId, projectId);

        TaskStatus status = request.getStatus() != null ? request.getStatus() : TaskStatus.TODO;
        int position = (int) taskRepository.countByProjectIdAndStatus(projectId, status);

        Task task = new Task(project, request.getTitle().trim(), request.getDescription(), status, position,
                userRepository.getReferenceById(currentUserId));

        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasks(Long workspaceId, Long projectId, Long currentUserId, TaskStatus status) {
        workspaceAccessService.requireMember(workspaceId, currentUserId);

        findProject(workspaceId, projectId);

        List<Task> tasks = status == null ? taskRepository.findByProjectIdOrderByCreatedAtAsc(projectId) : taskRepository.findByProjectIdAndStatusOrderByPositionAsc(projectId, status);

        return tasks.stream().map(taskMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long workspaceId, Long projectId, Long taskId, Long currentUserId) {
        workspaceAccessService.requireMember(workspaceId, currentUserId);

        findProject(workspaceId, projectId);

        return taskMapper.toResponse(findTask(projectId, taskId));
    }

    @Transactional
    public TaskResponse updateTask(Long workspaceId, Long projectId, Long taskId, Long currentUserId, UpdateTaskRequest request) {
        workspaceAccessService.requireMember(workspaceId, currentUserId);

        findProject(workspaceId, projectId);
        Task task = findTask(projectId, taskId);

        if (request.getTitle() == null && request.getDescription() == null) {
            throw new InvalidWorkspaceRequestException("Title or description must be provided");
        }

        if (request.getTitle() != null) {
            String title = request.getTitle().trim();

            if (title.isEmpty()) {
                throw new InvalidWorkspaceRequestException("Task title must not be blank");
            }

            task.setTitle(title);
        }

        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }

        Task savedTask = taskRepository.saveAndFlush(task);

        return taskMapper.toResponse(savedTask);
    }

    @Transactional
    public TaskResponse moveTask(Long workspaceId, Long projectId, Long taskId, Long currentUserId, MoveTaskRequest request) {
        workspaceAccessService.requireMember(workspaceId, currentUserId);

        lockProject(workspaceId, projectId);
        Task task = findTask(projectId, taskId);

        TaskStatus targetStatus = request.getStatus();

        List<Task> sourceColumn = loadColumn(projectId, task.getStatus());
        sourceColumn.removeIf(t -> t.getId().equals(task.getId()));

        List<Task> targetColumn = targetStatus == task.getStatus() ? sourceColumn : loadColumn(projectId, targetStatus);

        int index = request.getPosition() == null ? targetColumn.size() : Math.min(request.getPosition(), targetColumn.size());

        targetColumn.add(index, task);
        task.setStatus(targetStatus);

        renumber(sourceColumn);
        if (targetColumn != sourceColumn) {
            renumber(targetColumn);
        }

        taskRepository.flush();

        return taskMapper.toResponse(task);
    }

    @Transactional
    public void deleteTask(Long workspaceId, Long projectId, Long taskId, Long currentUserId) {
        WorkspaceMember member = workspaceAccessService.requireMember(workspaceId, currentUserId);

        lockProject(workspaceId, projectId);
        Task task = findTask(projectId, taskId);

        boolean isManager = member.getRole() == WorkspaceRole.OWNER || member.getRole() == WorkspaceRole.ADMIN;
        boolean isCreator = task.getCreatedBy() != null && task.getCreatedBy().getId().equals(currentUserId);

        if (!isManager && !isCreator) {
            throw new WorkspaceAccessDeniedException("You don't have permission to perform this action");
        }

        List<Task> column = loadColumn(projectId, task.getStatus());
        column.removeIf(t -> t.getId().equals(task.getId()));

        taskRepository.delete(task);
        renumber(column);
    }

    @Transactional(readOnly = true)
    public BoardResponse getBoard(Long workspaceId, Long projectId, Long currentUserId) {
        workspaceAccessService.requireMember(workspaceId, currentUserId);

        findProject(workspaceId, projectId);

        Map<TaskStatus, List<TaskResponse>> tasksByStatus = new EnumMap<>(TaskStatus.class);
        for (TaskStatus status : TaskStatus.values()) {
            tasksByStatus.put(status, new ArrayList<>());
        }

        for (Task task : taskRepository.findByProjectIdOrderByPositionAsc(projectId)) {
            tasksByStatus.get(task.getStatus()).add(taskMapper.toResponse(task));
        }

        List<BoardColumnResponse> columns = tasksByStatus.entrySet().stream()
                .map(entry -> new BoardColumnResponse(entry.getKey(), entry.getValue()))
                .toList();

        return new BoardResponse(projectId, columns);
    }

    private List<Task> loadColumn(Long projectId, TaskStatus status) {
        return new ArrayList<>(taskRepository.findByProjectIdAndStatusOrderByPositionAsc(projectId, status));
    }

    private void renumber(List<Task> column) {
        for (int i = 0; i < column.size(); i++) {
            Task task = column.get(i);
            if (task.getPosition() != i) {
                task.setPosition(i);
            }
        }
    }

    private Project findProject(Long workspaceId, Long projectId) {
        return projectRepository.findByIdAndWorkspaceId(projectId, workspaceId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));
    }

    private Project lockProject(Long workspaceId, Long projectId) {
        return projectRepository.findByIdAndWorkspaceIdForUpdate(projectId, workspaceId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));
    }

    private Task findTask(Long projectId, Long taskId) {
        return taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
    }
}
