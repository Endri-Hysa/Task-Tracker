package com.example.tasktracker.service;

import com.example.tasktracker.dto.TaskRequest;
import com.example.tasktracker.dto.TaskResponse;
import com.example.tasktracker.entity.Project;
import com.example.tasktracker.entity.Task;
import com.example.tasktracker.entity.User;
import com.example.tasktracker.entity.enums.TaskPriority;
import com.example.tasktracker.entity.enums.TaskStatus;
import com.example.tasktracker.exception.ResourceNotFoundException;
import com.example.tasktracker.repository.ProjectRepo;
import com.example.tasktracker.repository.TaskRepo;
import com.example.tasktracker.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepo taskRepository;


    @Mock
    private ProjectRepo projectRepository;

    @Mock
    private UserRepo userRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask_success() {
        User owner = new User();
        owner.setId(1L);
        owner.setUsername("admin");

        Project project = new Project();
        project.setId(1L);
        project.setOwner(owner);

        User assignee = new User();
        assignee.setId(1L);
        assignee.setUsername("endri");

        TaskRequest request = new TaskRequest();
        request.setTitle("Task i parë");
        request.setDescription("Pershkrimi");
        request.setStatus(TaskStatus.TODO);
        request.setPriority(TaskPriority.HIGH);
        request.setAssigneeId(1L);

        Task saved = new Task();
        saved.setId(1L);
        saved.setTitle("Task i parë");
        saved.setStatus(TaskStatus.TODO);
        saved.setPriority(TaskPriority.HIGH);
        saved.setProject(project);
        saved.setAssignee(assignee);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findById(1L)).thenReturn(Optional.of(assignee));
        when(taskRepository.save(any())).thenReturn(saved);

        TaskResponse response = taskService.createTask(1L, request, "admin");

        assertNotNull(response);
        assertEquals("Task i parë", response.getTitle());
        assertEquals(TaskStatus.TODO, response.getStatus());
        assertEquals(TaskPriority.HIGH, response.getPriority());
        verify(taskRepository, times(1)).save(any());
    }

    @Test
    void createTask_notOwner_throwsException() {
        User owner = new User();
        owner.setId(1L);
        owner.setUsername("endri");

        Project project = new Project();
        project.setId(1L);
        project.setOwner(owner);

        TaskRequest request = new TaskRequest();
        request.setTitle("Task i parë");
        request.setStatus(TaskStatus.TODO);
        request.setPriority(TaskPriority.HIGH);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThrows(IllegalArgumentException.class,
                () -> taskService.createTask(1L, request, "admin"));
        verify(taskRepository, never()).save(any());
    }

    @Test
    void createTask_projectNotFound_throwsException() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Task i parë");
        request.setStatus(TaskStatus.TODO);
        request.setPriority(TaskPriority.HIGH);

        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> taskService.createTask(99L, request,"admin"));
        verify(taskRepository, never()).save(any());
    }

    @Test
    void getById_notFound_throwsException() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> taskService.getById(99L));
    }

    @Test
    void getDueToday_returnsList() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Task sot");
        task.setStatus(TaskStatus.TODO);
        task.setPriority(TaskPriority.LOW);

        Project project = new Project();
        project.setId(1L);
        task.setProject(project);

        when(taskRepository.findTasksDueToday(LocalDate.now()))
                .thenReturn(List.of(task));

        List<TaskResponse> responses = taskService.getDueToday();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Task sot", responses.get(0).getTitle());
    }

    @Test
    void deleteTask_notFound_throwsException() {
        when(taskRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> taskService.deleteTask(99L));
        verify(taskRepository, never()).deleteById(any());
    }
}