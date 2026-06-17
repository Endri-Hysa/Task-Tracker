package com.example.tasktracker.controller;

import com.example.tasktracker.dto.ProjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@WithMockUser(username = "admin", roles = "ADMIN")
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long createUserAndGetId() throws Exception {
        String userJson = """
            {
                "username": "projuser",
                "email": "projuser@test.com",
                "password": "password123"
            }
            """;

        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    void createProject_shouldReturn201() throws Exception {
        Long ownerId = createUserAndGetId();

        ProjectRequest request = new ProjectRequest();
        request.setName("Projekti Im");
        request.setDescription("Pershkrimi");
        request.setOwnerId(ownerId);

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Projekti Im"))
                .andExpect(jsonPath("$.ownerUsername").value("projuser"));
    }

    @Test
    void createProject_invalidName_shouldReturn400() throws Exception {
        ProjectRequest request = new ProjectRequest();
        request.setName("ab");
        request.setOwnerId(1L);

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/projects/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteProject_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(delete("/api/projects/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProject_notFound_shouldReturn404() throws Exception {
        ProjectRequest request = new ProjectRequest();
        request.setName("Projekti i ri");
        request.setOwnerId(1L);

        mockMvc.perform(put("/api/projects/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}