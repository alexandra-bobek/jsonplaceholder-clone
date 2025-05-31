package com.jsonplaceholder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsonplaceholder.dto.UserDto;
import com.jsonplaceholder.model.User;
import com.jsonplaceholder.repository.UserRepository;
import com.jsonplaceholder.security.JwtTokenProvider;
import com.jsonplaceholder.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    private String authToken;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        // Очистка базы данных перед каждым тестом
        userRepository.deleteAll();

        // Создание тестового пользователя
        testUserDto = new UserDto();
        testUserDto.setName("Test User");
        testUserDto.setUsername("testuser");
        testUserDto.setEmail("test@example.com");
        testUserDto.setPhone("123-456-7890");
        testUserDto.setWebsite("test.com");

        // Создание тестового пользователя для principal
        User testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        UserPrincipal principal = new UserPrincipal(
            1L,
            "Test User",
            "test@example.com",
            "password",
            java.util.Collections.emptyList()
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        authToken = "Bearer " + tokenProvider.generateToken(authentication);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() throws Exception {
        // Arrange
        User user = new User();
        user.setName("Test User");
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        userRepository.save(user);

        // Act & Assert
        mockMvc.perform(get("/users")
                .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test User"))
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() throws Exception {
        // Arrange
        User user = new User();
        user.setName("Test User");
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        User savedUser = userRepository.save(user);

        // Act & Assert
        mockMvc.perform(get("/users/{id}", savedUser.getId())
                .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/users/{id}", 999L)
                .header("Authorization", authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_ShouldCreateAndReturnUser() throws Exception {
        // Act & Assert
        MvcResult result = mockMvc.perform(post("/users")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andReturn();

        UserDto createdUser = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
        assertNotNull(createdUser.getId());
    }

    @Test
    void updateUser_WhenUserExists_ShouldUpdateAndReturnUser() throws Exception {
        // Arrange
        User user = new User();
        user.setName("Old Name");
        user.setUsername("olduser");
        user.setEmail("old@example.com");
        User savedUser = userRepository.save(user);

        // Act & Assert
        mockMvc.perform(put("/users/{id}", savedUser.getId())
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void updateUser_WhenUserDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/users/{id}", 999L)
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDeleteUser() throws Exception {
        // Arrange
        User user = new User();
        user.setName("Test User");
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        User savedUser = userRepository.save(user);

        // Act & Assert
        mockMvc.perform(delete("/users/{id}", savedUser.getId())
                .header("Authorization", authToken))
                .andExpect(status().isNoContent());

        assertFalse(userRepository.existsById(savedUser.getId()));
    }

    @Test
    void deleteUser_WhenUserDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/users/{id}", 999L)
                .header("Authorization", authToken))
                .andExpect(status().isNotFound());
    }
} 