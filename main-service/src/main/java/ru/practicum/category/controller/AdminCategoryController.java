package ru.practicum.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.service.CategoryService;

@Slf4j
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.debug("Controller: createCategory with data {}", newCategoryDto);
        return categoryService.createCategory(newCategoryDto);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long categoryId) {
        log.debug("Controller: deleteCategory with id={}", categoryId);
        categoryService.deleteCategory(categoryId);
    }

    @PatchMapping("/{categoryId}")
    public CategoryDto updateCategory(
            @PathVariable Long categoryId,
            @RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.debug("Controller: updateCategory with id={} with data={}", categoryId, newCategoryDto);
        return categoryService.updateCategory(categoryId, newCategoryDto);
    }
}
