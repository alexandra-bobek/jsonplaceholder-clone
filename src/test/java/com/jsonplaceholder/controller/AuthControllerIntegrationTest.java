package com.jsonplaceholder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsonplaceholder.dto.LoginRequest;
import com.jsonplaceholder.dto.RegisterRequest;
import com.jsonplaceholder.model.AuthUser;
import com.jsonplaceholder.model.User;
import com.jsonplaceholder.repository.AuthUserRepository;
import com.jsonplaceholder.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        // Очистка базы данных перед каждым тестом
        authUserRepository.deleteAll();
        userRepository.deleteAll();

        // Подготовка тестовых данных для регистрации
        registerRequest = new RegisterRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setUsername("testuser");

        // Подготовка тестовых данных для входа
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
    }

    @Test
    void register_WithValidData_ShouldCreateUser() throws Exception {
        // Act & Assert
        MvcResult result = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.name").value("Test User"))
                .andExpect(jsonPath("$.user.username").value("testuser"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                .andReturn();

        // Verify user was created in database
        assertTrue(userRepository.existsByEmail("test@example.com"));
    }

    @Test
    void register_WithExistingEmail_ShouldReturnBadRequest() throws Exception {
        // Arrange
        User user = new User();
        user.setName("Existing User");
        user.setUsername("existinguser");
        user.setEmail("test@example.com");
        userRepository.save(user);

        AuthUser authUser = new AuthUser();
        authUser.setName("Existing User");
        authUser.setEmail("test@example.com");
        authUser.setPasswordHash(passwordEncoder.encode("password123"));
        authUser.setUser(user);
        authUserRepository.save(authUser);

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    @Test
    void register_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Arrange
        registerRequest.setEmail("invalid-email");

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_WithValidCredentials_ShouldReturnToken() throws Exception {
        // Arrange
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.email").value("test@example.com"));
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        // Arrange
        loginRequest.setPassword("wrongpassword");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_WithNonExistentUser_ShouldReturnUnauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
} 