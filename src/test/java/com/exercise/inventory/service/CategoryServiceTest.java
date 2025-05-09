package com.exercise.inventory.service;

import com.exercise.inventory.dto.CategoryDto;
import com.exercise.inventory.exception.ResourceNotFoundException;
import com.exercise.inventory.model.Category;
import com.exercise.inventory.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        categoryDto = new CategoryDto();
        categoryDto.setName("Electronics");
    }

    @Nested
    public class CreateCategoryTest {

        @Test
        void createCategory_ShouldReturnCreatedCategory() {
            // Arrange
            when(categoryRepository.save(any(Category.class))).thenReturn(category);

            // Act
            Category result = categoryService.createCategory(categoryDto);

            // Assert
            assertNotNull(result);
            assertEquals(category.getId(), result.getId());
            assertEquals(category.getName(), result.getName());
            verify(categoryRepository).save(any(Category.class));
        }
    }

    @Nested
    public class UpdateCategoryTest {

        @Test
        void updateCategory_WithExistingId_ShouldReturnUpdatedCategory() {
            // Arrange
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(categoryRepository.save(any(Category.class))).thenReturn(category);

            // Act
            Category result = categoryService.updateCategory(1L, categoryDto);

            // Assert
            assertNotNull(result);
            assertEquals(category.getId(), result.getId());
            assertEquals(category.getName(), result.getName());
            verify(categoryRepository).save(any(Category.class));
        }

        @Test
        void updateCategory_WithNonExistingId_ShouldThrowResourceNotFoundException() {
            // Arrange
            when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> {
                categoryService.updateCategory(1L, categoryDto);
            });
            verify(categoryRepository, never()).save(any(Category.class));
        }
    }

    @Nested
    public class DeleteCategoryTest {
        @Test
        void deleteCategory_WithExistingId_ShouldDeleteCategory() {
            // Arrange
            when(categoryRepository.existsById(1L)).thenReturn(true);
            doNothing().when(categoryRepository).deleteById(1L);

            // Act
            categoryService.deleteCategory(1L);

            // Assert
            verify(categoryRepository).deleteById(1L);
        }

        @Test
        void deleteCategory_WithNonExistingId_ShouldThrowResourceNotFoundException() {
            // Arrange
            when(categoryRepository.existsById(1L)).thenReturn(false);

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> {
                categoryService.deleteCategory(1L);
            });
            verify(categoryRepository, never()).deleteById(anyLong());
        }
    }

    @Nested
    public class FindCategoryTest {

        @Test
        void getCategoryById_WithExistingId_ShouldReturnCategory() {
            // Arrange
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

            // Act
            Category result = categoryService.getCategoryById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(category.getId(), result.getId());
            assertEquals(category.getName(), result.getName());
        }

        @Test
        void getCategoryById_WithNonExistingId_ShouldThrowResourceNotFoundException() {
            // Arrange
            when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> {
                categoryService.getCategoryById(1L);
            });
        }
    }

    @Nested
    public class FindCategoriesTest {
        @Test
        void getAllCategories_ShouldReturnAllCategories() {
            // Arrange
            List<Category> categories = Arrays.asList(category);
            when(categoryRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Arrays.asList(category)));

            // Act
            Page<Category> result = categoryService.getAllCategories(PageRequest.of(0, 10));

            // Assert
            assertEquals(1, result.getTotalElements());
            assertEquals(1, result.getTotalPages());
            assertTrue(result.stream().findFirst().isPresent());
            assertEquals(category.getId(), result.stream().findFirst().get().getId());
            assertEquals(category.getName(), result.stream().findFirst().get().getName());
        }

    }
}
