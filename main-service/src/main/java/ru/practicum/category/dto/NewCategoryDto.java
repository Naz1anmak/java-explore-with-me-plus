package ru.practicum.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewCategoryDto(
        @NotBlank(message = "Название категории не может быть пустой")
        @Size(min = 1, max = 50, message = "Название категории должно содержать от 3 до 50 символов")
        String name
) {
}
