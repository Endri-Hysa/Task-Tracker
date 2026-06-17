package com.example.tasktracker.dto;

import com.example.tasktracker.entity.enums.TaskPriority;
import com.example.tasktracker.entity.enums.TaskStatus;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private Long projectId;
    private Long assigneeId;
    private String assigneeUsername;
    private LocalDateTime createdAt;
}