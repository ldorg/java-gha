package com.example.template.service.impl;

import com.example.template.dto.UserDto;
import com.example.template.exception.ResourceAlreadyExistsException;
import com.example.template.exception.ResourceNotFoundException;
import com.example.template.model.User;
import com.example.template.repository.UserRepository;
import com.example.template.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto createUser(UserDto userDto, String password) {
        if (existsByUsername(userDto.getUsername())) {
            throw new ResourceAlreadyExistsException("Username already exists: " + userDto.getUsername());
        }
        if (existsByEmail(userDto.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists: " + userDto.getEmail());
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(true);

        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> getUserByUsername(String username) {
        return userRepository.findByUsername(username).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getActiveUsers(Pageable pageable) {
        return userRepository.findByActiveTrue(pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> searchUsers(String search, Pageable pageable) {
        return userRepository.findActiveUsersWithSearch(search, pageable).map(this::mapToDto);
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (!user.getUsername().equals(userDto.getUsername()) && existsByUsername(userDto.getUsername())) {
            throw new ResourceAlreadyExistsException("Username already exists: " + userDto.getUsername());
        }
        if (!user.getEmail().equals(userDto.getEmail()) && existsByEmail(userDto.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists: " + userDto.getEmail());
        }

        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());

        User updatedUser = userRepository.save(user);
        return mapToDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setActive(false);
        userRepository.save(user);
    }

    @Override
    public void activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setActive(true);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private UserDto mapToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getActive()
        );
    }
}