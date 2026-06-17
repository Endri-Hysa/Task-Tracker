package com.example.tasktracker.service;

import com.example.tasktracker.dto.ProjectRequest;
import com.example.tasktracker.dto.ProjectResponse;
import com.example.tasktracker.entity.Project;
import com.example.tasktracker.entity.User;
import com.example.tasktracker.exception.ResourceNotFoundException;
import com.example.tasktracker.repository.ProjectRepo;
import com.example.tasktracker.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepo projectRepository;

    @Mock
    private UserRepo userRepository;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void createProject_success() {
        User owner = new User();
        owner.setId(1L);
        owner.setUsername("endri");

        ProjectRequest request = new ProjectRequest();
        request.setName("Projekti Im");
        request.setDescription("Pershkrimi");
        request.setOwnerId(1L);

        Project saved = new Project();
        saved.setId(1L);
        saved.setName("Projekti Im");
        saved.setDescription("Pershkrimi");
        saved.setOwner(owner);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(projectRepository.save(any())).thenReturn(saved);

        ProjectResponse response = projectService.createProject(request);

        assertNotNull(response);
        assertEquals("Projekti Im", response.getName());
        assertEquals(1L, response.getOwnerId());
        assertEquals("endri", response.getOwnerUsername());
        verify(projectRepository, times(1)).save(any());
    }

    @Test
    void createProject_ownerNotFound_throwsException() {
        ProjectRequest request = new ProjectRequest();
        request.setName("Projekti Im");
        request.setOwnerId(99L);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> projectService.createProject(request));
        verify(projectRepository, never()).save(any());
    }

    @Test
    void getById_notFound_throwsException() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> projectService.getById(99L));
    }

    @Test
    void deleteProject_notFound_throwsException() {
        when(projectRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> projectService.deleteProject(99L));
        verify(projectRepository, never()).deleteById(any());
    }
}