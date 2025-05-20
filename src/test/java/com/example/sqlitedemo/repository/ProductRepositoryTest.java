package com.example.sqlitedemo.repository;

import com.example.sqlitedemo.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void findByNameContainingIgnoreCase_shouldReturnMatchingProducts() {
        // Arrange
        Product product1 = Product.builder()
                .name("iPhone")
                .description("Apple smartphone")
                .price(new BigDecimal("999.99"))
                .stockQuantity(50)
                .build();

        Product product2 = Product.builder()
                .name("Samsung Galaxy")
                .description("Samsung smartphone")
                .price(new BigDecimal("899.99"))
                .stockQuantity(30)
                .build();

        Product product3 = Product.builder()
                .name("Google Pixel")
                .description("Google smartphone")
                .price(new BigDecimal("799.99"))
                .stockQuantity(20)
                .build();

        entityManager.persist(product1);
        entityManager.persist(product2);
        entityManager.persist(product3);
        entityManager.flush();

        // Act
        List<Product> result = productRepository.findByNameContainingIgnoreCase("phone");

        // Assert
        assertTrue(result.isEmpty());

        // Act
        result = productRepository.findByNameContainingIgnoreCase("samsung");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Samsung Galaxy", result.get(0).getName());
    }
}