package com.exercise.inventory.service;

import com.exercise.inventory.dto.ProductDto;
import com.exercise.inventory.exception.ResourceNotFoundException;
import com.exercise.inventory.model.Category;
import com.exercise.inventory.model.Product;
import com.exercise.inventory.repository.CategoryRepository;
import com.exercise.inventory.repository.ProductRepository;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Transactional
    public Product createProduct(ProductDto productDto) {
        if (productDto.getVersion() != null) {
            throw new OptimisticLockException("New Products should not have a defined 'version'.");
        }

        Product product = mapDtoToEntity(productDto, new Product());
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, ProductDto productDto) {
        Product existingProduct = getProductById(id);

        if (productDto.getVersion() != null && !productDto.getVersion().equals(existingProduct.getVersion())) {
            throw new OptimisticLockException("Versions do not match. The product was already modified.");
        }

        Product updatedProduct = mapDtoToEntity(productDto, existingProduct);
        return productRepository.save(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    private Product mapDtoToEntity(ProductDto dto, Product product) {
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));
            product.setCategory(category);
        }

        return product;
    }
}
