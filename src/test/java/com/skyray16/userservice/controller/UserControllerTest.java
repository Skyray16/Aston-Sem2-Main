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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
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
    void createUser_WhenDataIsValid_ShouldReturn201AndLinks() throws Exception {
        UserRequestDto requestDto = new UserRequestDto("Иван", "ivan@test.com", 25);
        UserResponseDto responseDto = new UserResponseDto(1L, "Иван", "ivan@test.com", 25, LocalDateTime.now());

        when(userService.createUser(any(UserRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Иван"))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.all-users.href").exists());
    }

    @Test
    void createUser_WhenDataIsInvalid_ShouldReturn400() throws Exception {
        UserRequestDto invalidDto = new UserRequestDto("", "not-an-email", -5);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.email").exists());
        
        verify(userService, never()).createUser(any());
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturn200AndLinks() throws Exception {
        UserResponseDto responseDto = new UserResponseDto(1L, "Иван", "ivan@test.com", 25, LocalDateTime.now());
        
        when(userService.getUserById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("ivan@test.com"))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.delete.href").exists());
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldReturn404() throws Exception {
        when(userService.getUserById(99L)).thenThrow(new UserNotFoundException("Пользователь не найден"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Пользователь не найден"));
    }

    @Test
    void getAllUsers_ShouldReturnListWithEmbeddedAndLinks() throws Exception {
        UserResponseDto user1 = new UserResponseDto(1L, "Иван", "ivan@test.com", 25, LocalDateTime.now());
        UserResponseDto user2 = new UserResponseDto(2L, "Петр", "petr@test.com", 30, LocalDateTime.now());
        
        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.userResponseDtoList.size()").value(2))
                .andExpect(jsonPath("$._embedded.userResponseDtoList[0].name").value("Иван"))
                .andExpect(jsonPath("$._embedded.userResponseDtoList[1].name").value("Петр"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void updateUser_WhenDataIsValid_ShouldReturn200() throws Exception {
        UserRequestDto requestDto = new UserRequestDto("Иван Обновленный", "ivan_new@test.com", 26);
        UserResponseDto responseDto = new UserResponseDto(1L, "Иван Обновленный", "ivan_new@test.com", 26, LocalDateTime.now());

        when(userService.updateUser(eq(1L), any(UserRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Иван Обновленный"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void deleteUser_ShouldReturn204NoContent() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }
}