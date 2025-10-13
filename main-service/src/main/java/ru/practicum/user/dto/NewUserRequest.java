package ru.practicum.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record NewUserRequest(
        @NotBlank(message = "Имя пользователя не может быть пустым")
        String name,

        @NotBlank(message = "Email пользователя не может быть пустой")
        @Email
        String email
) {
}
