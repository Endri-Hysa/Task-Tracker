package com.example.tasktracker.service;

import com.example.tasktracker.dto.TaskActivityResponse;
import com.example.tasktracker.entity.Task;
import com.example.tasktracker.entity.TaskActivity;
import com.example.tasktracker.exception.ResourceNotFoundException;
import com.example.tasktracker.repository.TaskActivityRepo;
import com.example.tasktracker.repository.TaskRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskActivityService {

    private final TaskActivityRepo taskActivityRepository;
    private final TaskRepo taskRepository;

    public void logActivity(Task task, String action, String oldValue,
                            String newValue, String performedBy) {
        TaskActivity activity = new TaskActivity();
        activity.setTask(task);
        activity.setAction(action);
        activity.setOldValue(oldValue);
        activity.setNewValue(newValue);
        activity.setPerformedBy(performedBy);
        taskActivityRepository.save(activity);
    }

    public List<TaskActivityResponse> getActivitiesByTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task not found with id: " + taskId);
        }
        return taskActivityRepository.findByTaskIdOrderByPerformedAtDesc(taskId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private TaskActivityResponse toResponse(TaskActivity activity) {
        TaskActivityResponse response = new TaskActivityResponse();
        response.setId(activity.getId());
        response.setTaskId(activity.getTask().getId());
        response.setAction(activity.getAction());
        response.setOldValue(activity.getOldValue());
        response.setNewValue(activity.getNewValue());
        response.setPerformedBy(activity.getPerformedBy());
        response.setPerformedAt(activity.getPerformedAt());
        return response;
    }
}