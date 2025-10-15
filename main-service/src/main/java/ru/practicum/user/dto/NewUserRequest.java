package ru.practicum.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewUserRequest(
        @NotBlank(message = "Имя пользователя не может быть пустым")
        @Size(min = 2, max = 250)
        String name,

        @NotBlank(message = "Email пользователя не может быть пустой")
        @Email(message = "Email пользователя введен не верно")
        @Size(min = 6, max = 254)
        String email
) {
}
