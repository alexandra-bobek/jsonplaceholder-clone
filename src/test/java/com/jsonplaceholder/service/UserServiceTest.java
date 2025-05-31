package com.jsonplaceholder.service;

import com.jsonplaceholder.dto.AddressDto;
import com.jsonplaceholder.dto.CompanyDto;
import com.jsonplaceholder.dto.UserDto;
import com.jsonplaceholder.model.User;
import com.jsonplaceholder.repository.UserRepository;
import com.jsonplaceholder.service.impl.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto testUserDto;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Подготовка тестовых данных
        testUserDto = new UserDto();
        testUserDto.setName("Test User");
        testUserDto.setUsername("testuser");
        testUserDto.setEmail("test@example.com");
        testUserDto.setPhone("123-456-7890");
        testUserDto.setWebsite("test.com");

        AddressDto addressDto = new AddressDto();
        addressDto.setStreet("Test Street");
        addressDto.setSuite("Apt 123");
        addressDto.setCity("Test City");
        addressDto.setZipcode("12345");
        testUserDto.setAddress(addressDto);

        CompanyDto companyDto = new CompanyDto();
        companyDto.setName("Test Company");
        companyDto.setCatchPhrase("Test Catch Phrase");
        companyDto.setBs("Test BS");
        testUserDto.setCompany(companyDto);

        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));

        // Act
        List<UserDto> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getName(), result.get(0).getName());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        UserDto result = userService.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getName(), result.getName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void createUser_ShouldReturnCreatedUser() {
        // Arrange
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserDto result = userService.createUser(testUserDto);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getName(), result.getName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_WhenUserExists_ShouldReturnUpdatedUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserDto result = userService.updateUser(1L, testUserDto);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getName(), result.getName());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(1L, testUserDto));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDeleteUser() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(1L));
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, never()).deleteById(anyLong());
    }
} 