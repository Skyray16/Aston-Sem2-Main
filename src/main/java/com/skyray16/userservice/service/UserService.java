package com.skyray16.userservice.service;

import com.skyray16.userservice.dto.UserRequestDto;
import com.skyray16.userservice.dto.UserResponseDto;
import java.util.List;

public interface UserService {
    UserResponseDto createUser(UserRequestDto dto);
    UserResponseDto getUserById(Long id);
    List<UserResponseDto> getAllUsers();
    UserResponseDto updateUser(Long id, UserRequestDto dto);
    void deleteUser(Long id);
}