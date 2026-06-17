package com.example.tasktracker.service;

import com.example.tasktracker.dto.UserRequest;
import com.example.tasktracker.dto.UserResponse;
import com.example.tasktracker.entity.User;
import com.example.tasktracker.exception.ResourceNotFoundException;
import com.example.tasktracker.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepo userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_success() {
        UserRequest request = new UserRequest();
        request.setUsername("endri");
        request.setEmail("endri@test.com");
        request.setPassword("password123");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("endri");
        savedUser.setEmail("endri@test.com");

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(savedUser);

        UserResponse response = userService.createUser(request);

        assertNotNull(response);
        assertEquals("endri", response.getUsername());
        assertEquals("endri@test.com", response.getEmail());
        verify(userRepository, times(1)).save(any());
        
    }

    @Test
    void createUser_emailExists_throwsException() {
        UserRequest request = new UserRequest();
        request.setUsername("endri");
        request.setEmail("endri@test.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail(any())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getById_notFound_throwsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getById(99L));
    }
}