package com.example.tasktracker.repository;

import com.example.tasktracker.entity.Project;
import com.example.tasktracker.entity.Task;
import com.example.tasktracker.entity.User;
import com.example.tasktracker.entity.enums.TaskPriority;
import com.example.tasktracker.entity.enums.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepo taskRepository;

    @Autowired
    private ProjectRepo projectRepository;

    @Autowired
    private UserRepo userRepository;

    private User createUser() {
        User user = new User();
        user.setUsername("endri");
        user.setEmail("endri@test.com");
        user.setPassword("password123");
        return userRepository.save(user);
    }

    private Project createProject(User owner) {
        Project project = new Project();
        project.setName("Projekti Im");
        project.setOwner(owner);
        return projectRepository.save(project);
    }

    @Test
    void findByProjectId_success() {
        User user = createUser();
        Project project = createProject(user);

        Task task = new Task();
        task.setTitle("Task i parë");
        task.setStatus(TaskStatus.TODO);
        task.setPriority(TaskPriority.HIGH);
        task.setProject(project);
        taskRepository.save(task);

        var tasks = taskRepository.findByProjectId(project.getId(),
                org.springframework.data.domain.Pageable.unpaged());

        assertEquals(1, tasks.getTotalElements());
        assertEquals("Task i parë", tasks.getContent().get(0).getTitle());
    }

    @Test
    void findTasksDueToday_success() {
        User user = createUser();
        Project project = createProject(user);

        Task task = new Task();
        task.setTitle("Task sot");
        task.setStatus(TaskStatus.TODO);
        task.setPriority(TaskPriority.LOW);
        task.setDueDate(LocalDate.now());
        task.setProject(project);
        taskRepository.save(task);

        List<Task> tasks = taskRepository.findTasksDueToday(LocalDate.now());

        assertEquals(1, tasks.size());
        assertEquals("Task sot", tasks.get(0).getTitle());
    }

    @Test
    void findByAssigneeId_success() {
        User user = createUser();
        Project project = createProject(user);

        Task task = new Task();
        task.setTitle("Task i caktuar");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setPriority(TaskPriority.MEDIUM);
        task.setProject(project);
        task.setAssignee(user);
        taskRepository.save(task);

        List<Task> tasks = taskRepository.findByAssigneeId(user.getId());

        assertEquals(1, tasks.size());
        assertEquals("Task i caktuar", tasks.get(0).getTitle());
    }
}