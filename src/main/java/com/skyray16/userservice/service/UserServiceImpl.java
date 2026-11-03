package com.skyray16.userservice.service;

import com.skyray16.userservice.dto.UserRequestDto;
import com.skyray16.userservice.dto.UserResponseDto;
import com.skyray16.userservice.entity.User;
import com.skyray16.userservice.exception.UserNotFoundException;
import com.skyray16.userservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;

    // Ручной конструктор для внедрения зависимости (вместо Lombok)
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestDto dto) {
        log.info("Создание пользователя с email: {}", dto.email());
        
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }
        
        User user = new User(dto.name(), dto.email(), dto.age());
        User saved = userRepository.save(user);
        return mapToDto(saved);
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + id + " не найден"));
        return mapToDto(user);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(Long id, UserRequestDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        
        // Проверяем, не занят ли новый email другим пользователем
        if (!user.getEmail().equals(dto.email()) && userRepository.findByEmail(dto.email()).isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }

        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setAge(dto.age());
        
        return mapToDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        userRepository.deleteById(id);
    }

    // Преобразование Entity -> DTO
    private UserResponseDto mapToDto(User user) {
        return new UserResponseDto(
                user.getId(), 
                user.getName(), 
                user.getEmail(), 
                user.getAge(), 
                user.getCreatedAt()
        );
    }
}