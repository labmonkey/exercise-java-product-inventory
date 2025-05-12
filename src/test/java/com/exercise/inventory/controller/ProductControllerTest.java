package com.exercise.inventory.controller;

import com.exercise.inventory.dto.ProductDto;
import com.exercise.inventory.model.Category;
import com.exercise.inventory.model.Product;
import com.exercise.inventory.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private Product testProduct;
    private Category testCategory;
    private ProductDto testProductDto;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Test Category");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setQuantity(10);
        testProduct.setVersion(0L);
        testProduct.setCategory(testCategory);

        testProductDto = new ProductDto();
        testProductDto.setName("Test Product");
        testProductDto.setDescription("Test Description");
        testProductDto.setPrice(new BigDecimal("99.99"));
        testProductDto.setQuantity(10);
        testProductDto.setCategoryId(testCategory.getId());
    }

    @Test
    @WithMockUser
    void getAllProducts_ShouldReturnProductsList() throws Exception {
        List<Product> products = Arrays.asList(testProduct);
        Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 10), products.size());

        when(productService.getAllProducts(any(Pageable.class))).thenReturn(productPage);

        mockMvc.perform(get("/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(testProduct.getId()))
                .andExpect(jsonPath("$.content[0].name").value(testProduct.getName()));
    }

    @Test
    @WithMockUser
    void getProductById_WithValidId_ShouldReturnProduct() throws Exception {
        when(productService.getProductById(anyLong())).thenReturn(testProduct);

        mockMvc.perform(get("/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testProduct.getId()))
                .andExpect(jsonPath("$.name").value(testProduct.getName()));
    }

    @Test
    @WithMockUser(roles = "FULL")
    void createProduct_WithFullRole_ShouldReturnCreatedProduct() throws Exception {
        when(productService.createProduct(any(ProductDto.class))).thenReturn(testProduct);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProductDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testProduct.getId()))
                .andExpect(jsonPath("$.name").value(testProduct.getName()));
    }

    @Test
    @WithMockUser(roles = "READER")
    void createProduct_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProductDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "FULL")
    void updateProduct_WithFullRole_ShouldReturnUpdatedProduct() throws Exception {
        when(productService.updateProduct(anyLong(), any(ProductDto.class))).thenReturn(testProduct);

        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProductDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testProduct.getId()))
                .andExpect(jsonPath("$.name").value(testProduct.getName()));
    }

    @Test
    @WithMockUser(roles = "FULL")
    void updateProduct_WithOptimisticLockException_ShouldReturnConflict() throws Exception {
        when(productService.updateProduct(anyLong(), any(ProductDto.class)))
                .thenThrow(new OptimisticLockException("The product was modified by another user"));

        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProductDto)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "READER")
    void updateProduct_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProductDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "FULL")
    void deleteProduct_WithFullRole_ShouldReturnNoContent() throws Exception {
        doNothing().when(productService).deleteProduct(anyLong());

        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "READER")
    void deleteProduct_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void sortProducts_ShouldReturnSortedProducts() throws Exception {
        List<Product> products = Arrays.asList(testProduct);
        Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 10), products.size());

        when(productService.getAllProducts(any(Pageable.class))).thenReturn(productPage);

        mockMvc.perform(get("/products")
                        .param("sortBy", "price")
                        .param("sortDir", "DESC")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(testProduct.getId()))
                .andExpect(jsonPath("$.content[0].name").value(testProduct.getName()));
    }
}
