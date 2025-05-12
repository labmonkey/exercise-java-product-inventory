package com.exercise.inventory.controller;

import com.exercise.inventory.dto.ProductDto;
import com.exercise.inventory.exception.ErrorResponse;
import com.exercise.inventory.model.Product;
import com.exercise.inventory.service.ProductService;
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
@RequestMapping("/products")
@Tag(name = "Products", description = "Endpoints for managing products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieves a paginated list of products. Requires authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of products"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Page<Product>> getAllProducts(
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "Field to sort by (e.g., 'id', 'name', 'price')", example = "id") @RequestParam(required = false, defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (ASC or DESC)", example = "ASC") @RequestParam(defaultValue = "ASC") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, pageSize, sort);

        Page<Product> products = productService.getAllProducts(pageable);

        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieves a specific product by its ID. Requires authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved product",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Product> getProductById(@Parameter(description = "ID of the product to retrieve") @PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new product", description = "Creates a new product. Requires FULL role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product successfully created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "400", description = "Invalid product data provided",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "404", description = "Category not found for product",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient role", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductDto productDto) {
        Product createdProduct = productService.createProduct(productDto);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing product", description = "Updates an existing product by its ID. Requires FULL role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product successfully updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "400", description = "Invalid product data provided",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "404", description = "Product or Category not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflict due to optimistic locking",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient role", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Product> updateProduct(@Parameter(description = "ID of the product to update") @PathVariable Long id, @Valid @RequestBody ProductDto productDto) {
        Product updatedProduct = productService.updateProduct(id, productDto);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", description = "Deletes a product by its ID. Requires FULL role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient role", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteProduct(@Parameter(description = "ID of the product to delete") @PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
