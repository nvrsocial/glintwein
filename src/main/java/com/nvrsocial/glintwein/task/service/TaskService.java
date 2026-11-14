package com.nvrsocial.glintwein.task.service;

import com.nvrsocial.glintwein.activity.dto.TaskActivityResponse;
import com.nvrsocial.glintwein.activity.enums.TaskActivityType;
import com.nvrsocial.glintwein.activity.service.TaskActivityService;
import com.nvrsocial.glintwein.common.exception.InvalidWorkspaceRequestException;
import com.nvrsocial.glintwein.common.exception.ProjectNotFoundException;
import com.nvrsocial.glintwein.common.exception.TaskNotFoundException;
import com.nvrsocial.glintwein.common.exception.WorkspaceAccessDeniedException;
import com.nvrsocial.glintwein.notification.enums.NotificationType;
import com.nvrsocial.glintwein.notification.service.NotificationService;
import com.nvrsocial.glintwein.project.entity.Project;
import com.nvrsocial.glintwein.project.repository.ProjectRepository;
import com.nvrsocial.glintwein.task.component.TaskMapper;
import com.nvrsocial.glintwein.task.dto.*;
import com.nvrsocial.glintwein.task.entity.Task;
import com.nvrsocial.glintwein.task.enums.TaskPriority;
import com.nvrsocial.glintwein.task.enums.TaskStatus;
import com.nvrsocial.glintwein.task.repository.TaskRepository;
import com.nvrsocial.glintwein.task.repository.TaskSpecifications;
import com.nvrsocial.glintwein.user.entity.User;
import com.nvrsocial.glintwein.workspace.entity.WorkspaceMember;
import com.nvrsocial.glintwein.workspace.enums.WorkspaceRole;
import com.nvrsocial.glintwein.workspace.repository.WorkspaceMemberRepository;
import com.nvrsocial.glintwein.workspace.service.WorkspaceAccessService;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceAccessService workspaceAccessService;
    private final TaskActivityService taskActivityService;
    private final NotificationService notificationService;
    private final TaskMapper taskMapper;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository, WorkspaceMemberRepository workspaceMemberRepository,
                       WorkspaceAccessService workspaceAccessService, TaskActivityService taskActivityService,
                       NotificationService notificationService, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.workspaceAccessService = workspaceAccessService;
        this.taskActivityService = taskActivityService;
        this.notificationService = notificationService;
        this.taskMapper = taskMapper;
    }

    @Transactional
    public TaskResponse createTask(Long workspaceId, Long projectId, Long currentUserId, TaskRequest request) {
        WorkspaceMember member = workspaceAccessService.requireMember(workspaceId, currentUserId);
        User actor = member.getUser();

        Project project = lockProject(workspaceId, projectId);

        User assignee = request.getAssigneeId() == null ? null : requireAssignee(workspaceId, request.getAssigneeId());
        TaskStatus status = request.getStatus() != null ? request.getStatus() : TaskStatus.TODO;
        TaskPriority priority = request.getPriority() != null ? request.getPriority() : TaskPriority.MEDIUM;
        int position = (int) taskRepository.countByProjectIdAndStatus(projectId, status);

        Task task = taskRepository.save(new Task(project, request.getTitle().trim(), request.getDescription(), status, priority,
                position, actor, assignee));

        taskActivityService.record(task, actor, TaskActivityType.CREATED);

        if (assignee != null) {
            notifyAssignee(task, actor, assignee, workspaceId);
        }

        return taskMapper.toResponse(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasks(Long workspaceId, Long projectId, Long currentUserId, TaskStatus status, TaskPriority priority, Long assigneeId) {
        workspaceAccessService.requireMember(workspaceId, currentUserId);

        findProject(workspaceId, projectId);

        Specification<Task> specification = TaskSpecifications.inProject(projectId);
        if (status != null) {
            specification = specification.and(TaskSpecifications.hasStatus(status));
        }
        if (priority != null) {
            specification = specification.and(TaskSpecifications.hasPriority(priority));
        }
        if (assigneeId != null) {
            specification = specification.and(TaskSpecifications.hasAssignee(assigneeId));
        }

        Sort sort = status != null ? Sort.by("position") : Sort.by("createdAt", "id");

        return taskRepository.findAll(specification, sort).stream().map(taskMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long workspaceId, Long projectId, Long taskId, Long currentUserId) {
        workspaceAccessService.requireMember(workspaceId, currentUserId);

        return taskMapper.toResponse(requireTask(workspaceId, projectId, taskId));
    }

    @Transactional
    public TaskResponse updateTask(Long workspaceId, Long projectId, Long taskId, Long currentUserId, UpdateTaskRequest request) {
        WorkspaceMember member = workspaceAccessService.requireMember(workspaceId, currentUserId);
        User actor = member.getUser();

        Task task = requireTask(workspaceId, projectId, taskId);

        if (request.getTitle() == null && request.getDescription() == null && request.getPriority() == null) {
            throw new InvalidWorkspaceRequestException("Title, description or priority must be provided");
        }

        if (request.getTitle() != null) {
            String title = request.getTitle().trim();

            if (title.isEmpty()) {
                throw new InvalidWorkspaceRequestException("Task title must not be blank");
            }

            if (!title.equals(task.getTitle())) {
                taskActivityService.record(task, actor, TaskActivityType.TITLE_CHANGED, task.getTitle(), title);
                task.setTitle(title);
            }
        }

        if (request.getDescription() != null && !request.getDescription().equals(task.getDescription())) {
            taskActivityService.record(task, actor, TaskActivityType.DESCRIPTION_CHANGED);
            task.setDescription(request.getDescription());
        }

        if (request.getPriority() != null && request.getPriority() != task.getPriority()) {
            taskActivityService.record(task, actor, TaskActivityType.PRIORITY_CHANGED, task.getPriority().name(), request.getPriority().name());
            task.setPriority(request.getPriority());
        }

        Task savedTask = taskRepository.saveAndFlush(task);

        return taskMapper.toResponse(savedTask);
    }

    @Transactional
    public TaskResponse assignTask(Long workspaceId, Long projectId, Long taskId, Long currentUserId, AssignTaskRequest request) {
        WorkspaceMember member = workspaceAccessService.requireMember(workspaceId, currentUserId);
        User actor = member.getUser();

        Task task = requireTask(workspaceId, projectId, taskId);

        User newAssignee = request.getAssigneeId() == null ? null : requireAssignee(workspaceId, request.getAssigneeId());
        Long oldAssigneeId = task.getAssignee() == null ? null : task.getAssignee().getId();

        if (Objects.equals(oldAssigneeId, request.getAssigneeId())) {
            return taskMapper.toResponse(task);
        }

        task.setAssignee(newAssignee);
        taskActivityService.record(task, actor, TaskActivityType.ASSIGNEE_CHANGED, idToString(oldAssigneeId), idToString(request.getAssigneeId()));

        if (newAssignee != null) {
            notifyAssignee(task, actor, newAssignee, workspaceId);
        }

        return taskMapper.toResponse(taskRepository.saveAndFlush(task));
    }

    @Transactional
    public TaskResponse moveTask(Long workspaceId, Long projectId, Long taskId, Long currentUserId, MoveTaskRequest request) {
        WorkspaceMember member = workspaceAccessService.requireMember(workspaceId, currentUserId);
        User actor = member.getUser();

        lockProject(workspaceId, projectId);
        Task task = findTask(projectId, taskId);

        TaskStatus oldStatus = task.getStatus();
        TaskStatus targetStatus = request.getStatus();

        List<Task> sourceColumn = loadColumn(projectId, oldStatus);
        sourceColumn.removeIf(t -> t.getId().equals(task.getId()));

        List<Task> targetColumn = targetStatus == oldStatus ? sourceColumn : loadColumn(projectId, targetStatus);

        int index = request.getPosition() == null ? targetColumn.size() : Math.min(request.getPosition(), targetColumn.size());

        targetColumn.add(index, task);
        task.setStatus(targetStatus);

        renumber(sourceColumn);
        if (targetColumn != sourceColumn) {
            renumber(targetColumn);
        }

        if (targetStatus != oldStatus) {
            taskActivityService.record(task, actor, TaskActivityType.STATUS_CHANGED, oldStatus.name(), targetStatus.name());
            notificationService.notifyTaskParticipants(task, actor, NotificationType.TASK_STATUS_CHANGED,
                    actor.getUsername() + " moved task \"" + task.getTitle() + "\" to " + targetStatus.name());
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

    @Transactional(readOnly = true)
    public List<TaskActivityResponse> getTaskActivity(Long workspaceId, Long projectId, Long taskId, Long currentUserId) {
        workspaceAccessService.requireMember(workspaceId, currentUserId);

        requireTask(workspaceId, projectId, taskId);

        return taskActivityService.getTaskActivity(taskId);
    }

    @Transactional
    public void unassignMemberTasks(Long workspaceId, Long userId, User actor) {
        for (Task task : taskRepository.findByAssigneeIdAndProjectWorkspaceId(userId, workspaceId)) {
            task.setAssignee(null);
            taskActivityService.record(task, actor, TaskActivityType.ASSIGNEE_CHANGED, idToString(userId), null);
        }
    }

    public Task requireTask(Long workspaceId, Long projectId, Long taskId) {
        findProject(workspaceId, projectId);

        return findTask(projectId, taskId);
    }

    private void notifyAssignee(Task task, User actor, User assignee, Long workspaceId) {
        notificationService.notifyUser(assignee.getId(), actor, NotificationType.TASK_ASSIGNED,
                actor.getUsername() + " assigned you to task \"" + task.getTitle() + "\"",
                workspaceId, task.getProject().getId(), task.getId());
    }

    private User requireAssignee(Long workspaceId, Long assigneeId) {
        return workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, assigneeId)
                .map(WorkspaceMember::getUser)
                .orElseThrow(() -> new InvalidWorkspaceRequestException("Assignee must be a member of this workspace"));
    }

    private String idToString(Long id) {
        return id == null ? null : id.toString();
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
