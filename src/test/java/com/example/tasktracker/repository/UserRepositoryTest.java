package com.example.tasktracker.repository;

import com.example.tasktracker.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepo userRepository;

    @Test
    void existsByEmail_shouldReturnTrue() {
        User user = new User();
        user.setUsername("endri");
        user.setEmail("endri@test.com");
        user.setPassword("password123");
        userRepository.save(user);

        assertTrue(userRepository.existsByEmail("endri@test.com"));
        assertFalse(userRepository.existsByEmail("tjeter@test.com"));
    }

    @Test
    void existsByUsername_shouldReturnTrue() {
        User user = new User();
        user.setUsername("endri");
        user.setEmail("endri@test.com");
        user.setPassword("password123");
        userRepository.save(user);

        assertTrue(userRepository.existsByUsername("endri"));
        assertFalse(userRepository.existsByUsername("unknown"));
    }
}