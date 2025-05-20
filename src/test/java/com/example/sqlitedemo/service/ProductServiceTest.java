package com.example.sqlitedemo.service;

import com.example.sqlitedemo.model.Product;
import com.example.sqlitedemo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("19.99"))
                .stockQuantity(10)
                .build();
    }

    @Test
    void getAllProducts_shouldReturnAllProducts() {
        // Arrange
        List<Product> productList = Arrays.asList(testProduct);
        when(productRepository.findAll()).thenReturn(productList);

        // Act
        List<Product> result = productService.getAllProducts();

        // Assert
        assertEquals(1, result.size());
        assertEquals(testProduct, result.get(0));
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProductById_whenProductExists_shouldReturnProduct() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act
        Optional<Product> result = productService.getProductById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testProduct, result.get());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_whenProductDoesNotExist_shouldReturnEmpty() {
        // Arrange
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Product> result = productService.getProductById(99L);

        // Assert
        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById(99L);
    }

    @Test
    void searchProducts_shouldReturnMatchingProducts() {
        // Arrange
        List<Product> productList = Arrays.asList(testProduct);
        when(productRepository.findByNameContainingIgnoreCase("Test")).thenReturn(productList);

        // Act
        List<Product> result = productService.searchProducts("Test");

        // Assert
        assertEquals(1, result.size());
        assertEquals(testProduct, result.get(0));
        verify(productRepository, times(1)).findByNameContainingIgnoreCase("Test");
    }

    @Test
    void createProduct_shouldReturnSavedProduct() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        Product result = productService.createProduct(testProduct);

        // Assert
        assertNotNull(result);
        assertEquals(testProduct, result);
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    void updateProduct_whenProductExists_shouldReturnUpdatedProduct() {
        // Arrange
        Product updatedProduct = Product.builder()
                .id(1L)
                .name("Updated Product")
                .description("Updated Description")
                .price(new BigDecimal("29.99"))
                .stockQuantity(20)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act
        Optional<Product> result = productService.updateProduct(1L, updatedProduct);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Updated Product", result.get().getName());
        assertEquals("Updated Description", result.get().getDescription());
        assertEquals(new BigDecimal("29.99"), result.get().getPrice());
        assertEquals(20, result.get().getStockQuantity());

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_whenProductDoesNotExist_shouldReturnEmpty() {
        // Arrange
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Product> result = productService.updateProduct(99L, testProduct);

        // Assert
        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById(99L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_whenProductExists_shouldReturnTrue() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        doNothing().when(productRepository).delete(testProduct);

        // Act
        boolean result = productService.deleteProduct(1L);

        // Assert
        assertTrue(result);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(testProduct);
    }

    @Test
    void deleteProduct_whenProductDoesNotExist_shouldReturnFalse() {
        // Arrange
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        boolean result = productService.deleteProduct(99L);

        // Assert
        assertFalse(result);
        verify(productRepository, times(1)).findById(99L);
        verify(productRepository, never()).delete(any());
    }
}
