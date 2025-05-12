package com.exercise.inventory.controller;

import com.exercise.inventory.dto.CategoryDto;
import com.exercise.inventory.model.Category;
import com.exercise.inventory.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService CategoryService;

    @Autowired
    public CategoryController(CategoryService CategoryService) {
        this.CategoryService = CategoryService;
    }

    @GetMapping
    public ResponseEntity<Page<Category>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, pageSize, sort);

        Page<Category> categories = CategoryService.getAllCategories(pageable);

        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(CategoryService.getCategoryById(id));
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody CategoryDto CategoryDto) {
        Category createdCategory = CategoryService.createCategory(CategoryDto);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDto CategoryDto) {
        Category updatedCategory = CategoryService.updateCategory(id, CategoryDto);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        CategoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
