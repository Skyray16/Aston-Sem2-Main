package com.skyray16.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyray16.userservice.dto.UserRequestDto;
import com.skyray16.userservice.dto.UserResponseDto;
import com.skyray16.userservice.exception.UserNotFoundException;
import com.skyray16.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void createUser_ShouldReturnCreated() throws Exception {
        UserRequestDto request = new UserRequestDto("Иван", "ivan@test.com", 25);
        UserResponseDto response = new UserResponseDto(1L, "Иван", "ivan@test.com", 25, LocalDateTime.now());

        when(userService.createUser(any(UserRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Иван"))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getUserById_WhenExists_ShouldReturnUser() throws Exception {
        UserResponseDto response = new UserResponseDto(1L, "Иван", "ivan@test.com", 25, LocalDateTime.now());
        when(userService.getUserById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ivan@test.com"));
    }

    @Test
    void getUserById_WhenNotFound_ShouldReturn404() throws Exception {
        when(userService.getUserById(99L)).thenThrow(new UserNotFoundException("Не найден"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Не найден"));
    }

    @Test
    void createUser_WithInvalidData_ShouldReturn400() throws Exception {
        UserRequestDto invalidRequest = new UserRequestDto("", "invalid-email", -5);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.email").exists());
    }
}