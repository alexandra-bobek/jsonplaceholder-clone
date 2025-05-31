package com.jsonplaceholder.service;

import com.jsonplaceholder.dto.JwtResponse;
import com.jsonplaceholder.dto.LoginRequest;
import com.jsonplaceholder.dto.RegisterRequest;
import com.jsonplaceholder.model.AuthUser;
import com.jsonplaceholder.model.User;
import com.jsonplaceholder.repository.AuthUserRepository;
import com.jsonplaceholder.repository.UserRepository;
import com.jsonplaceholder.security.JwtTokenProvider;
import com.jsonplaceholder.service.impl.AuthServiceImpl;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AuthUser authUser;
    private User user;
    private String testToken = "test.jwt.token";

    @BeforeEach
    void setUp() {
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

        // Подготовка тестовых данных пользователя
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        authUser = new AuthUser();
        authUser.setId(1L);
        authUser.setName("Test User");
        authUser.setEmail("test@example.com");
        authUser.setPasswordHash("encodedPassword");
        authUser.setUser(user);
    }

    @Test
    void register_WhenEmailNotExists_ShouldCreateUserAndReturnJwtResponse() {
        // Arrange
        when(authUserRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(authUserRepository.save(any(AuthUser.class))).thenReturn(authUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn(testToken);

        // Act
        JwtResponse response = authService.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals(testToken, response.getToken());
        assertEquals(authUser.getId(), response.getUser().getId());
        assertEquals(authUser.getName(), response.getUser().getName());
        assertEquals(authUser.getUser().getUsername(), response.getUser().getUsername());
        assertEquals(authUser.getEmail(), response.getUser().getEmail());
        
        verify(authUserRepository).existsByEmail(registerRequest.getEmail());
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(authUserRepository).save(any(AuthUser.class));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider).generateToken(authentication);
    }

    @Test
    void register_WhenEmailExists_ShouldThrowException() {
        // Arrange
        when(authUserRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(EntityExistsException.class, () -> authService.register(registerRequest));
        verify(authUserRepository).existsByEmail(registerRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
        verify(authUserRepository, never()).save(any(AuthUser.class));
    }

    @Test
    void login_ShouldReturnJwtResponse() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn(testToken);
        when(authUserRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(authUser));

        // Act
        JwtResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(testToken, response.getToken());
        assertEquals(authUser.getId(), response.getUser().getId());
        assertEquals(authUser.getName(), response.getUser().getName());
        assertEquals(authUser.getUser().getUsername(), response.getUser().getUsername());
        assertEquals(authUser.getEmail(), response.getUser().getEmail());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider).generateToken(authentication);
        verify(authUserRepository).findByEmail(loginRequest.getEmail());
    }
} 