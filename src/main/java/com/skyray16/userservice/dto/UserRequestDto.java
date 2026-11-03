package com.skyray16.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record UserRequestDto(
        @NotBlank(message = "Имя не может быть пустым") String name,
        @NotBlank(message = "Email не может быть пустым") @Email(message = "Некорректный формат email") String email,
        @Min(value = 1, message = "Возраст должен быть больше 0") Integer age
) {}