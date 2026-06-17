package com.example.tasktracker.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class TaskActivityResponse {
    private Long id;
    private Long taskId;
    private String action;
    private String oldValue;
    private String newValue;
    private String performedBy;
    private LocalDateTime performedAt;
}