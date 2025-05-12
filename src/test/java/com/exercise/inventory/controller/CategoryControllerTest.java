package com.exercise.inventory.controller;

import com.exercise.inventory.dto.CategoryDto;
import com.exercise.inventory.model.Category;
import com.exercise.inventory.model.Product;
import com.exercise.inventory.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    private Category testCategory;
    private CategoryDto testCategoryDto;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Test Category");

        testCategoryDto = new CategoryDto();
        testCategoryDto.setName("Test Category");
    }

    @Test
    @WithMockUser
    void getAllCategories_ShouldReturnCategoriesList() throws Exception {
        List<Category> categories = Arrays.asList(testCategory);
        Page<Category> productPage = new PageImpl<>(categories, PageRequest.of(0, 10), categories.size());

        when(categoryService.getAllCategories(any(Pageable.class))).thenReturn(productPage);

        mockMvc.perform(get("/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(testCategory.getId()))
                .andExpect(jsonPath("$.content[0].name").value(testCategory.getName()));
    }

    @Test
    @WithMockUser
    void getCategoryById_WithValidId_ShouldReturnCategory() throws Exception {
        when(categoryService.getCategoryById(anyLong())).thenReturn(testCategory);

        mockMvc.perform(get("/categories/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testCategory.getId()))
                .andExpect(jsonPath("$.name").value(testCategory.getName()));
    }

    @Test
    @WithMockUser(roles = "FULL")
    void createCategory_WithAdminRole_ShouldReturnCreatedCategory() throws Exception {
        when(categoryService.createCategory(any(CategoryDto.class))).thenReturn(testCategory);

        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCategoryDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testCategory.getId()))
                .andExpect(jsonPath("$.name").value(testCategory.getName()));
    }
    
    @Test
    @WithMockUser(roles = "READER")
    void createCategory_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCategoryDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "FULL")
    void updateCategory_WithAdminRole_ShouldReturnUpdatedCategory() throws Exception {
        when(categoryService.updateCategory(anyLong(), any(CategoryDto.class))).thenReturn(testCategory);

        mockMvc.perform(put("/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCategoryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testCategory.getId()))
                .andExpect(jsonPath("$.name").value(testCategory.getName()));
    }
    
    @Test
    @WithMockUser(roles = "READER")
    void updateCategory_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(put("/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCategoryDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "FULL")
    void deleteCategory_WithAdminRole_ShouldReturnNoContent() throws Exception {
        doNothing().when(categoryService).deleteCategory(anyLong());

        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isNoContent());
    }
    
    @Test
    @WithMockUser(roles = "READER")
    void deleteCategory_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isForbidden());
    }
}
