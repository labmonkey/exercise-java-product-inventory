package com.exercise.inventory.service;

import com.exercise.inventory.dto.CategoryDto;
import com.exercise.inventory.exception.ResourceNotFoundException;
import com.exercise.inventory.model.Category;
import com.exercise.inventory.repository.CategoryRepository;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public Page<Category> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    @Transactional
    public Category createCategory(CategoryDto categoryDto) {
        if (categoryDto.getVersion() != null) {
            throw new OptimisticLockException("New Categories should not have a defined 'version'.");
        }

        Category category = mapDtoToEntity(categoryDto, new Category());
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, CategoryDto categoryDto) {
        Category existingCategory = getCategoryById(id);

        if (categoryDto.getVersion() != null && !categoryDto.getVersion().equals(existingCategory.getVersion())) {
            throw new OptimisticLockException("Versions do not match. The category was already modified.");
        }

        Category updatedCategory = mapDtoToEntity(categoryDto, existingCategory);
        return categoryRepository.save(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    private Category mapDtoToEntity(CategoryDto dto, Category category) {
        category.setName(dto.getName());

        return category;
    }
}
