package com.exercise.inventory.service;

import com.exercise.inventory.dto.ProductDto;
import com.exercise.inventory.exception.ResourceNotFoundException;
import com.exercise.inventory.model.Category;
import com.exercise.inventory.model.Product;
import com.exercise.inventory.repository.CategoryRepository;
import com.exercise.inventory.repository.ProductRepository;
import jakarta.persistence.OptimisticLockException;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductDto productDto;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("99.99"));
        product.setQuantity(10);
        product.setVersion(0L);
        product.setCategory(category);

        productDto = new ProductDto();
        productDto.setName("Updated Product");
        productDto.setDescription("Updated Description");
        productDto.setPrice(new BigDecimal("149.99"));
        productDto.setQuantity(20);
        productDto.setCategoryId(1L);
    }

    @Nested
    public class CreateProductTest {

        @Test
        void createProduct_WithEmptyVersion_ShouldCreateProduct() {
            // Arrange
            when(productRepository.save(any(Product.class))).thenReturn(product);
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

            // Act
            Product updatedProduct = productService.createProduct(productDto);

            // Assert
            assertNotNull(updatedProduct);
            verify(productRepository).save(any(Product.class));
        }

        @Test
        void createProduct_WithDefinedVersion_ShouldThrowOptimisticLockException() {
            // Arrange
            productDto.setVersion(1L);

            // Act & Assert
            assertThrows(OptimisticLockException.class, () -> {
                productService.createProduct(productDto);
            });
            verify(productRepository, never()).save(any(Product.class));
        }

        @Test
        void createProduct_WithNonExistentCategory_ShouldThrowResourceNotFoundException() {
            // Arrange
            when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> {
                productService.createProduct(productDto);
            });
        }

    }

    @Nested
    public class UpdateProductTest {

        @Test
        void updateProduct_WithCorrectVersion_ShouldUpdateProduct() {
            // Arrange
            productDto.setVersion(0L);
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(productRepository.save(any(Product.class))).thenReturn(product);

            // Act
            Product updatedProduct = productService.updateProduct(1L, productDto);

            // Assert
            assertNotNull(updatedProduct);
            verify(productRepository).save(any(Product.class));
        }

        @Test
        void updateProduct_WithIncorrectVersion_ShouldThrowOptimisticLockException() {
            // Arrange
            productDto.setVersion(1L);
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            // Act & Assert
            assertThrows(OptimisticLockException.class, () -> {
                productService.updateProduct(1L, productDto);
            });
            verify(productRepository, never()).save(any(Product.class));
        }

        @Test
        void updateProduct_WithNonExistentProduct_ShouldThrowResourceNotFoundException() {
            // Arrange
            when(productRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> {
                productService.updateProduct(1L, productDto);
            });
        }

        @Test
        void updateProduct_WithNonExistentCategory_ShouldThrowResourceNotFoundException() {
            // Arrange
            productDto.setVersion(0L);
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> {
                productService.updateProduct(1L, productDto);
            });
        }
    }

    @Nested
    public class DeleteProductTest {
        @Test
        void deleteProduct_WithExistentProduct_ShouldDeleteProduct() {
            // Arrange
            when(productRepository.existsById(1L)).thenReturn(true);

            // Act & Assert
            productService.deleteProduct(1L);
        }

        @Test
        void deleteProduct_WithNonExistentProduct_ShouldThrowResourceNotFoundException() {
            // Arrange
            when(productRepository.existsById(1L)).thenReturn(false);

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> {
                productService.deleteProduct(1L);
            });
        }
    }

    @Nested
    public class FindProductTest {

        @Test
        void getProductById_WithExistingId_ShouldReturnProduct() {
            // Arrange
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            // Act
            Product result = productService.getProductById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(product.getId(), result.getId());
            assertEquals(product.getName(), result.getName());
        }

        @Test
        void getProductById_WithNonExistingId_ShouldThrowResourceNotFoundException() {
            // Arrange
            when(productRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> {
                productService.getProductById(1L);
            });
        }
    }

    @Nested
    public class FindProductsTest {
        @Test
        void getAllProducts_ShouldReturnAllProducts() {
            // Arrange
            List<Product> products = Arrays.asList(product);
            when(productRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Arrays.asList(product)));

            // Act
            Page<Product> result = productService.getAllProducts(PageRequest.of(0, 10));

            // Assert
            assertEquals(1, result.getTotalElements());
            assertEquals(1, result.getTotalPages());
            assertTrue(result.stream().findFirst().isPresent());
            assertEquals(product.getId(), result.stream().findFirst().get().getId());
            assertEquals(product.getName(), result.stream().findFirst().get().getName());
        }

    }
}
