package com.example.sqlitedemo.controller;

import com.example.sqlitedemo.model.Product;
import com.example.sqlitedemo.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

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
    @WithMockUser
    void getAllProducts_shouldReturnAllProducts() throws Exception {
        // Arrange
        List<Product> productList = Arrays.asList(testProduct);
        when(productService.getAllProducts()).thenReturn(productList);

        // Act & Assert
        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Product")))
                .andExpect(jsonPath("$[0].description", is("Test Description")))
                .andExpect(jsonPath("$[0].price", is(19.99)))
                .andExpect(jsonPath("$[0].stockQuantity", is(10)));
    }

    @Test
    @WithMockUser
    void getProductById_whenProductExists_shouldReturnProduct() throws Exception {
        // Arrange
        when(productService.getProductById(1L)).thenReturn(Optional.of(testProduct));

        // Act & Assert
        mockMvc.perform(get("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.price", is(19.99)))
                .andExpect(jsonPath("$.stockQuantity", is(10)));
    }

    @Test
    @WithMockUser
    void getProductById_whenProductDoesNotExist_shouldReturnNotFound() throws Exception {
        // Arrange
        when(productService.getProductById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/products/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void searchProducts_shouldReturnMatchingProducts() throws Exception {
        // Arrange
        List<Product> productList = Arrays.asList(testProduct);
        when(productService.searchProducts("Test")).thenReturn(productList);

        // Act & Assert
        mockMvc.perform(get("/api/products/search")
                        .param("name", "Test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Test Product")));
    }

    @Test
    @WithMockUser
    void createProduct_shouldReturnCreatedProduct() throws Exception {
        // Arrange
        when(productService.createProduct(any(Product.class))).thenReturn(testProduct);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.price", is(19.99)))
                .andExpect(jsonPath("$.stockQuantity", is(10)));
    }

    @Test
    @WithMockUser
    void updateProduct_whenProductExists_shouldReturnUpdatedProduct() throws Exception {
        // Arrange
        Product updatedProduct = Product.builder()
                .id(1L)
                .name("Updated Product")
                .description("Updated Description")
                .price(new BigDecimal("29.99"))
                .stockQuantity(20)
                .build();

        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(Optional.of(updatedProduct));

        // Act & Assert
        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Product")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.price", is(29.99)))
                .andExpect(jsonPath("$.stockQuantity", is(20)));
    }

    @Test
    @WithMockUser
    void updateProduct_whenProductDoesNotExist_shouldReturnNotFound() throws Exception {
        // Arrange
        when(productService.updateProduct(eq(99L), any(Product.class))).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/products/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void deleteProduct_whenProductExists_shouldReturnNoContent() throws Exception {
        // Arrange
        when(productService.deleteProduct(1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void deleteProduct_whenProductDoesNotExist_shouldReturnNotFound() throws Exception {
        // Arrange
        when(productService.deleteProduct(99L)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/products/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}