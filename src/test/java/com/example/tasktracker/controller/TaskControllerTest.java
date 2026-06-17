package com.example.tasktracker.controller;

import com.example.tasktracker.dto.ProjectRequest;
import com.example.tasktracker.dto.TaskRequest;
import com.example.tasktracker.dto.UserRequest;
import com.example.tasktracker.entity.enums.TaskPriority;
import com.example.tasktracker.entity.enums.TaskStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@WithMockUser(username = "admin", roles = "ADMIN")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long projectId;
    private Long userId;

    @BeforeEach
    void setup() throws Exception {
        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setName("Projekti Test " + UUID.randomUUID());
        projectRequest.setDescription("Pershkrimi");
        projectRequest.setOwnerId(1L);

        String projectResponse = mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        projectId = objectMapper.readTree(projectResponse).get("id").asLong();
        userId = 1L;
    }

    @Test
    void createTask_shouldReturn201() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("Task i parë");
        request.setDescription("Pershkrimi");
        request.setStatus(TaskStatus.TODO);
        request.setPriority(TaskPriority.HIGH);
        request.setDueDate(LocalDate.now());
        request.setAssigneeId(userId);

        mockMvc.perform(post("/api/projects/" + projectId + "/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Task i parë"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    void createTask_invalidTitle_shouldReturn400() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("ab");
        request.setStatus(TaskStatus.TODO);
        request.setPriority(TaskPriority.HIGH);

        mockMvc.perform(post("/api/projects/" + projectId + "/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/tasks/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getDueToday_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/tasks/due-today"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTask_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(delete("/api/tasks/999"))
                .andExpect(status().isNotFound());
    }
}