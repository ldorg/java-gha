package com.example.template.controller;

import com.example.template.dto.UserDto;
import com.example.template.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setUsername("testuser");
        testUserDto.setEmail("test@example.com");
        testUserDto.setActive(true);
        testUserDto.setCreatedAt(LocalDateTime.now());
        testUserDto.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @WithMockUser
    void createUser_Success() throws Exception {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("username", "testuser");
        request.put("email", "test@example.com");
        request.put("password", "password123");

        when(userService.createUser(any(UserDto.class), anyString())).thenReturn(testUserDto);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @WithMockUser
    void createUser_MissingPassword_BadRequest() throws Exception {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("username", "testuser");
        request.put("email", "test@example.com");

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getUserById_Found() throws Exception {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUserDto));

        // Act & Assert
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser
    void getUserById_NotFound() throws Exception {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getAllUsers_Success() throws Exception {
        // Arrange
        PageImpl<UserDto> page = new PageImpl<>(Collections.singletonList(testUserDto), PageRequest.of(0, 20), 1);
        when(userService.getActiveUsers(any())).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].username").value("testuser"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser
    void searchUsers_Success() throws Exception {
        // Arrange
        PageImpl<UserDto> page = new PageImpl<>(Collections.singletonList(testUserDto), PageRequest.of(0, 20), 1);
        when(userService.searchUsers(anyString(), any())).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/users").param("search", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].username").value("testuser"));
    }

    @Test
    @WithMockUser
    void updateUser_Success() throws Exception {
        // Arrange
        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setId(1L);
        updatedUserDto.setUsername("updateduser");
        updatedUserDto.setEmail("updated@example.com");

        when(userService.updateUser(eq(1L), any(UserDto.class))).thenReturn(updatedUserDto);

        // Act & Assert
        mockMvc.perform(put("/api/users/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updateduser"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    @WithMockUser
    void deleteUser_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/users/1")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void deactivateUser_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(patch("/api/users/1/deactivate")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void activateUser_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(patch("/api/users/1/activate")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
}