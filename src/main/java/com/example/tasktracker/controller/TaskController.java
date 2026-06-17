package com.example.tasktracker.controller;

import com.example.tasktracker.dto.TaskRequest;
import com.example.tasktracker.dto.TaskResponse;
import com.example.tasktracker.entity.enums.TaskStatus;
import com.example.tasktracker.service.TaskActivityService;
import com.example.tasktracker.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import com.example.tasktracker.dto.TaskActivityResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import com.example.tasktracker.entity.enums.TaskPriority;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TaskController {
    private final TaskActivityService taskActivityService;
    private final TaskService taskService;

    @PostMapping("/projects/{projectId}/tasks")
    @Operation(summary = "Krijo detyrë të re në projekt")
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable Long projectId,
            @Valid @RequestBody TaskRequest request,
            Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(projectId, request, principal.getName()));
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getById(id));
    }

    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<Page<TaskResponse>> getByProject(
            @PathVariable Long projectId,
            @RequestParam(required = false) TaskStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(taskService.getByProject(projectId, status, pageable));
    }

    @PutMapping("/tasks/{id}")
    @Operation(summary = "Përditëso detyrën")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request,
            Principal principal) {
        return ResponseEntity.ok(taskService.updateTask(id, request, principal.getName()));
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tasks/due-today")
    public ResponseEntity<List<TaskResponse>> getDueToday() {
        return ResponseEntity.ok(taskService.getDueToday());
    }

    @GetMapping("/users/{userId}/tasks")
    public ResponseEntity<List<TaskResponse>> getByAssignee(@PathVariable Long userId) {
        return ResponseEntity.ok(taskService.getByAssignee(userId));
    }
    @GetMapping("/tasks/search")
    @Operation(summary = "Kërko tasks sipas titullit, statusit dhe prioritetit")
    public ResponseEntity<Page<TaskResponse>> searchTasks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            Pageable pageable) {
        return ResponseEntity.ok(taskService.searchTasks(title, status, priority, pageable));
    }
    @GetMapping("/tasks/{id}/activities")
    @Operation(summary = "Merr historinë e ndryshimeve të një task")
    public ResponseEntity<List<TaskActivityResponse>> getActivities(@PathVariable Long id) {
        return ResponseEntity.ok(taskActivityService.getActivitiesByTask(id));
    }
}