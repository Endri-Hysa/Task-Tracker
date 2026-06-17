package com.example.tasktracker.service;

import com.example.tasktracker.dto.TaskRequest;
import com.example.tasktracker.dto.TaskResponse;
import com.example.tasktracker.entity.Project;
import com.example.tasktracker.entity.Task;
import com.example.tasktracker.entity.User;
import com.example.tasktracker.entity.enums.TaskStatus;
import com.example.tasktracker.exception.ResourceNotFoundException;
import com.example.tasktracker.repository.ProjectRepo;
import com.example.tasktracker.repository.TaskRepo;
import com.example.tasktracker.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import com.example.tasktracker.entity.enums.TaskPriority;
import com.example.tasktracker.repository.TaskSpecification;
import org.springframework.data.jpa.domain.Specification;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final EmailService emailService;
    private final TaskRepo taskRepository;
    private final ProjectRepo projectRepository;
    private final UserRepo userRepository;
    private final TaskActivityService taskActivityService;

    public TaskResponse createTask(Long projectId, TaskRequest request, String username) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        if (!project.getOwner().getUsername().equals(username)) {
            throw new IllegalArgumentException("Only the project owner can add tasks!");
        }

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setProject(project);

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            task.setAssignee(assignee);

            try {
                emailService.sendTaskAssignmentEmail(
                        assignee.getEmail(),
                        assignee.getUsername(),
                        task.getTitle(),
                        project.getName()
                );
            } catch (Exception e) {
                System.out.println("Email nuk u dërgua: " + e.getMessage());
            }
        }

        Task saved = taskRepository.save(task);
        return toResponse(saved);
    }

    public TaskResponse getById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return toResponse(task);
    }

    public Page<TaskResponse> getByProject(Long projectId, TaskStatus status, Pageable pageable) {
        if (status != null) {
            return taskRepository.findByProjectIdAndStatus(projectId, status, pageable)
                    .map(this::toResponse);
        }
        return taskRepository.findByProjectId(projectId, pageable)
                .map(this::toResponse);
    }

    public TaskResponse updateTask(Long id, TaskRequest request, String performedBy) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        if (!task.getStatus().equals(request.getStatus())) {
            taskActivityService.logActivity(
                    task,
                    "STATUS_CHANGED",
                    task.getStatus().name(),
                    request.getStatus().name(),
                    performedBy
            );
        }

        if (!task.getPriority().equals(request.getPriority())) {
            taskActivityService.logActivity(
                    task,
                    "PRIORITY_CHANGED",
                    task.getPriority().name(),
                    request.getPriority().name(),
                    performedBy
            );
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            String oldAssignee = task.getAssignee() != null ? task.getAssignee().getUsername() : "none";
            if (!oldAssignee.equals(assignee.getUsername())) {
                taskActivityService.logActivity(
                        task,
                        "ASSIGNEE_CHANGED",
                        oldAssignee,
                        assignee.getUsername(),
                        performedBy
                );
            }
            task.setAssignee(assignee);
        }

        Task saved = taskRepository.save(task);
        return toResponse(saved);
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    public List<TaskResponse> getDueToday() {
        return taskRepository.findTasksDueToday(LocalDate.now())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<TaskResponse> getByAssignee(Long userId) {
        return taskRepository.findByAssigneeId(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public Page<TaskResponse> searchTasks(String title, TaskStatus status, TaskPriority priority, Pageable pageable) {
        Specification<Task> spec = Specification.allOf(
                TaskSpecification.hasTitle(title),
                TaskSpecification.hasStatus(status),
                TaskSpecification.hasPriority(priority)
        );

        return taskRepository.findAll(spec, pageable).map(this::toResponse);
    }
    private TaskResponse toResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setDueDate(task.getDueDate());
        response.setProjectId(task.getProject().getId());
        response.setCreatedAt(task.getCreatedAt());
        if (task.getAssignee() != null) {
            response.setAssigneeId(task.getAssignee().getId());
            response.setAssigneeUsername(task.getAssignee().getUsername());
        }
        return response;
    }
}