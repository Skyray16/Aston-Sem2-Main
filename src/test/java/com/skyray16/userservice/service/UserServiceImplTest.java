package com.skyray16.userservice.service;

import com.skyray16.userservice.dao.UserDao;
import com.skyray16.userservice.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDao userDao;
    @InjectMocks
    private UserServiceImpl userService; 

    @Test
    void createUser_ShouldCallDaoSave() {
        // Arrange (Подготовка)
        User user = new User("Тест", "test@test.com", 25);
        when(userDao.save(any(User.class))).thenReturn(user);

        // Act (Действие)
        User result = userService.createUser(user);

        // Assert (Проверка)
        assertNotNull(result);
        assertEquals("Тест", result.getName());
        verify(userDao, times(1)).save(user); 
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        User user = new User("Найденный", "found@test.com", 30);
        when(userDao.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("Найденный", result.get().getName());
        verify(userDao).findById(1L);
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldReturnEmpty() {
        when(userDao.findById(99L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(99L);

        assertFalse(result.isPresent());
        verify(userDao).findById(99L);
    }

    @Test
    void deleteUser_ShouldCallDaoDelete() {
        doNothing().when(userDao).deleteById(1L);

        userService.deleteUser(1L);

        verify(userDao, times(1)).deleteById(1L);
    }
}