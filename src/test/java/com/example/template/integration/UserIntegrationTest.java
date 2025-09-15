package com.example.template.integration;

import com.example.template.Application;
import com.example.template.dto.UserDto;
import com.example.template.model.User;
import com.example.template.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase
@TestPropertySource(locations = "classpath:application-test.yml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser
    @Transactional
    void createUser_FullFlow_Success() throws Exception {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("username", "integrationtest");
        request.put("email", "integration@test.com");
        request.put("password", "password123");

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("integrationtest"))
                .andExpect(jsonPath("$.email").value("integration@test.com"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.id").exists());

        // Verify in database
        assert(userRepository.existsByUsername("integrationtest"));
        assert(userRepository.existsByEmail("integration@test.com"));
    }

    @Test
    @WithMockUser
    @Transactional
    void getUserById_AfterCreation_Success() throws Exception {
        // Arrange - Create user first
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedpassword");
        user.setActive(true);
        User savedUser = userRepository.save(user);

        // Act & Assert
        mockMvc.perform(get("/api/users/" + savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @WithMockUser
    @Transactional
    void getAllUsers_WithPagination_Success() throws Exception {
        // Arrange - Create multiple users
        for (int i = 1; i <= 5; i++) {
            User user = new User();
            user.setUsername("user" + i);
            user.setEmail("user" + i + "@example.com");
            user.setPassword("password");
            user.setActive(true);
            userRepository.save(user);
        }

        // Act & Assert
        mockMvc.perform(get("/api/users")
                .param("page", "0")
                .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    @WithMockUser
    @Transactional
    void searchUsers_Success() throws Exception {
        // Arrange
        User user1 = new User();
        user1.setUsername("johnsmith");
        user1.setEmail("john@example.com");
        user1.setPassword("password");
        user1.setActive(true);
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("janedoe");
        user2.setEmail("jane@example.com");
        user2.setPassword("password");
        user2.setActive(true);
        userRepository.save(user2);

        // Act & Assert - Search by username
        mockMvc.perform(get("/api/users")
                .param("search", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].username").value("johnsmith"));
    }

    @Test
    @WithMockUser
    @Transactional
    void updateUser_Success() throws Exception {
        // Arrange - Create user first
        User user = new User();
        user.setUsername("originaluser");
        user.setEmail("original@example.com");
        user.setPassword("password");
        user.setActive(true);
        User savedUser = userRepository.save(user);

        UserDto updateRequest = new UserDto();
        updateRequest.setUsername("updateduser");
        updateRequest.setEmail("updated@example.com");

        // Act & Assert
        mockMvc.perform(put("/api/users/" + savedUser.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updateduser"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));

        // Verify in database
        User updatedUser = userRepository.findById(savedUser.getId()).orElseThrow();
        assert(updatedUser.getUsername().equals("updateduser"));
        assert(updatedUser.getEmail().equals("updated@example.com"));
    }

    @Test
    @WithMockUser
    @Transactional
    void deactivateUser_Success() throws Exception {
        // Arrange
        User user = new User();
        user.setUsername("activeuser");
        user.setEmail("active@example.com");
        user.setPassword("password");
        user.setActive(true);
        User savedUser = userRepository.save(user);

        // Act & Assert
        mockMvc.perform(patch("/api/users/" + savedUser.getId() + "/deactivate")
                .with(csrf()))
                .andExpect(status().isNoContent());

        // Verify in database
        User deactivatedUser = userRepository.findById(savedUser.getId()).orElseThrow();
        assert(!deactivatedUser.getActive());
    }
}