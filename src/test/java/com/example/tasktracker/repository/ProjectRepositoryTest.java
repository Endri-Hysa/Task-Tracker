package com.example.tasktracker.repository;

import com.example.tasktracker.entity.Project;
import com.example.tasktracker.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepo projectRepository;

    @Autowired
    private UserRepo userRepository;

    @Test
    void saveProject_success() {
        User owner = new User();
        owner.setUsername("endri");
        owner.setEmail("endri@test.com");
        owner.setPassword("password123");
        userRepository.save(owner);

        Project project = new Project();
        project.setName("Projekti Im");
        project.setDescription("Pershkrimi");
        project.setOwner(owner);

        Project saved = projectRepository.save(project);

        assertNotNull(saved.getId());
        assertEquals("Projekti Im", saved.getName());
        assertEquals("endri", saved.getOwner().getUsername());
    }

    @Test
    void deleteProject_success() {
        User owner = new User();
        owner.setUsername("endri");
        owner.setEmail("endri@test.com");
        owner.setPassword("password123");
        userRepository.save(owner);

        Project project = new Project();
        project.setName("Projekti Im");
        project.setOwner(owner);
        Project saved = projectRepository.save(project);

        projectRepository.deleteById(saved.getId());

        assertFalse(projectRepository.existsById(saved.getId()));
    }
}