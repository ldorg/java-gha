package com.example.template.service;

import com.example.template.dto.UserDto;
import com.example.template.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {

    UserDto createUser(UserDto userDto, String password);
    
    Optional<UserDto> getUserById(Long id);
    
    Optional<UserDto> getUserByUsername(String username);
    
    Optional<UserDto> getUserByEmail(String email);
    
    Page<UserDto> getAllUsers(Pageable pageable);
    
    Page<UserDto> getActiveUsers(Pageable pageable);
    
    Page<UserDto> searchUsers(String search, Pageable pageable);
    
    UserDto updateUser(Long id, UserDto userDto);
    
    void deleteUser(Long id);
    
    void deactivateUser(Long id);
    
    void activateUser(Long id);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}