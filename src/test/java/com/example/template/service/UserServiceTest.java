package com.example.template.service;

import com.example.template.dto.UserDto;
import com.example.template.exception.ResourceAlreadyExistsException;
import com.example.template.exception.ResourceNotFoundException;
import com.example.template.model.User;
import com.example.template.repository.UserRepository;
import com.example.template.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedpassword");
        testUser.setActive(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setUsername("testuser");
        testUserDto.setEmail("test@example.com");
        testUserDto.setActive(true);
        testUserDto.setCreatedAt(testUser.getCreatedAt());
        testUserDto.setUpdatedAt(testUser.getUpdatedAt());
    }

    @Test
    void createUser_Success() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedpassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserDto result = userService.createUser(testUserDto, "password123");

        // Assert
        assertNotNull(result);
        assertEquals(testUserDto.getUsername(), result.getUsername());
        assertEquals(testUserDto.getEmail(), result.getEmail());
        assertTrue(result.getActive());
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void createUser_UsernameExists_ThrowsException() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // Act & Assert
        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class, 
            () -> userService.createUser(testUserDto, "password123"));
        assertTrue(exception.getMessage().contains("Username already exists"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_EmailExists_ThrowsException() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class, 
            () -> userService.createUser(testUserDto, "password123"));
        assertTrue(exception.getMessage().contains("Email already exists"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_Found() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        Optional<UserDto> result = userService.getUserById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testUser.getUsername(), result.get().getUsername());
        assertEquals(testUser.getEmail(), result.get().getEmail());
    }

    @Test
    void getUserById_NotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<UserDto> result = userService.getUserById(1L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void getUserByUsername_Found() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        Optional<UserDto> result = userService.getUserByUsername("testuser");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testUser.getUsername(), result.get().getUsername());
    }

    @Test
    void deleteUser_Success() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);

        // Act
        assertDoesNotThrow(() -> userService.deleteUser(1L));

        // Assert
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_NotFound_ThrowsException() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
            () -> userService.deleteUser(1L));
        assertTrue(exception.getMessage().contains("User not found"));
        verify(userRepository, never()).deleteById(1L);
    }

    @Test
    void deactivateUser_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        assertDoesNotThrow(() -> userService.deactivateUser(1L));

        // Assert
        verify(userRepository).save(argThat(user -> !user.getActive()));
    }

    @Test
    void activateUser_Success() {
        // Arrange
        testUser.setActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        assertDoesNotThrow(() -> userService.activateUser(1L));

        // Assert
        verify(userRepository).save(argThat(User::getActive));
    }
}