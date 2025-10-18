package ru.practicum.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.category.model.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("""
            SELECT c FROM Category c
            WHERE c.name = :name"""
    )
    Optional<Category> findByName(@Param("name") String name);

    @Query("""
            SELECT c FROM Category c
            WHERE c.name = :name AND c.id != :id"""
    )
    Optional<Category> findByNameAndIdNot(@Param("name") String name, @Param("id") Long id);
}
