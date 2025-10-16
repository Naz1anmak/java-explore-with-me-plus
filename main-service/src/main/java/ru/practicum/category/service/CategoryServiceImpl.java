package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        validateCategoryName(newCategoryDto.name());
        checkCategoryNameUniqueForCreate(newCategoryDto.name());

        Category category = categoryMapper.toEntity(newCategoryDto);
        Category savedCategory = categoryRepository.save(category);
        log.info("Создана новая категория: {}", savedCategory);
        return categoryMapper.toDto(savedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = getCategoryByIdOrThrow(categoryId);

        if (eventRepository.existsByCategoryId(categoryId)) {
            throw new ConflictException("Невозможно удалить категорию с существующими событиями");
        }

        categoryRepository.delete(category);
        log.info("Удалена категория с ID: {}", categoryId);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long categoryId, NewCategoryDto newCategoryDto) {
        validateCategoryName(newCategoryDto.name());

        Category category = getCategoryByIdOrThrow(categoryId);
        checkCategoryNameUniqueForUpdate(newCategoryDto.name(), categoryId);

        categoryMapper.updateCategoryFromDto(newCategoryDto, category);
        Category updatedCategory = categoryRepository.save(category);

        log.info("Обновлена категория: {}", updatedCategory);
        return categoryMapper.toDto(updatedCategory);
    }

    @Override
    public List<CategoryDto> getCategories(Pageable pageable) {
        Page<Category> categoriesPage = categoryRepository.findAll(pageable);

        if (categoriesPage.isEmpty()) {
            return List.of();
        }

        List<CategoryDto> result = categoriesPage.stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());

        log.info("Найдено {} категорий", result.size());
        return result;
    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        Category category = getCategoryByIdOrThrow(categoryId);
        CategoryDto result = categoryMapper.toDto(category);

        log.info("Найдена категория: {}", result);
        return result;
    }

    private void validateCategoryName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Название категории не может быть пустым");
        }
        if (name.length() > 50) {
            throw new ValidationException("Название категории не может превышать 50 символов");
        }
    }

    private void checkCategoryNameUniqueForCreate(String name) {
        categoryRepository.findByName(name)
                .ifPresent(category -> {
                    throw new ConflictException("Категория с названием '" + name + "' уже существует");
                });
    }

    private void checkCategoryNameUniqueForUpdate(String name, Long excludedId) {
        categoryRepository.findByNameAndIdNot(name, excludedId)
                .ifPresent(category -> {
                    throw new ConflictException("Категория с названием '" + name + "' уже существует");
                });
    }

    private Category getCategoryByIdOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Категория с id " + categoryId + " не найдена"));
    }
}
