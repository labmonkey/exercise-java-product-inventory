package com.exercise.inventory.controller;

import com.exercise.inventory.dto.CategoryDto;
import com.exercise.inventory.exception.ErrorResponse;
import com.exercise.inventory.model.Category;
import com.exercise.inventory.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Categories", description = "Endpoints for managing categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(summary = "Get all categories", description = "Retrieves a paginated list of categories. Requires authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of categories"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Page<Category>> getAllCategories(
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "Field to sort by (e.g., 'id', 'name')", example = "id") @RequestParam(required = false, defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (ASC or DESC)", example = "ASC") @RequestParam(defaultValue = "ASC") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, pageSize, sort);

        Page<Category> categories = categoryService.getAllCategories(pageable);

        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Retrieves a specific category by its ID. Requires authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved category",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Category> getCategoryById(@Parameter(description = "ID of the category to retrieve") @PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new category", description = "Creates a new category. Requires FULL role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category successfully created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "400", description = "Invalid category data provided",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))), // GlobalExceptionHandler for MethodArgumentNotValidException returns Object
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient role", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        Category createdCategory = categoryService.createCategory(categoryDto);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing category", description = "Updates an existing category by its ID. Requires FULL role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category successfully updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "400", description = "Invalid category data provided",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient role", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Category> updateCategory(@Parameter(description = "ID of the category to update") @PathVariable Long id, @Valid @RequestBody CategoryDto categoryDto) {
        Category updatedCategory = categoryService.updateCategory(id, categoryDto);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category", description = "Deletes a category by its ID. Requires FULL role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient role", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteCategory(@Parameter(description = "ID of the category to delete") @PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
