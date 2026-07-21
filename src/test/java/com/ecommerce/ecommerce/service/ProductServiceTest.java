package com.ecommerce.ecommerce.service;

import com.ecommerce.ecommerce.dto.request.ProductRequest;
import com.ecommerce.ecommerce.dto.response.ProductResponse;
import com.ecommerce.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce.model.Product;
import com.ecommerce.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product mockProduct;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        mockProduct = Product.builder()
                .id(1L)
                .name("Test Laptop")
                .description("A fast laptop")
                .price(BigDecimal.valueOf(999.99))
                .stockQuantity(10)
                .build();

        productRequest = new ProductRequest();
        productRequest.setName("Test Laptop");
        productRequest.setDescription("A fast laptop");
        productRequest.setPrice(BigDecimal.valueOf(999.99));
        productRequest.setStockQuantity(10);
    }

    @Test
    void getAllProducts_Success() {
        when(productRepository.findAll()).thenReturn(List.of(mockProduct));

        List<ProductResponse> responses = productService.getAllProducts();

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
        assertEquals("Test Laptop", responses.get(0).getName());
    }

    @Test
    void getProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));

        ProductResponse response = productService.getProductById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Laptop", response.getName());
    }

    @Test
    void getProductById_ThrowsResourceNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L));
    }

    @Test
    void createProduct_Success() {
        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);

        ProductResponse response = productService.createProduct(productRequest);

        assertNotNull(response);
        assertEquals("Test Laptop", response.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);

        ProductRequest updateReq = new ProductRequest();
        updateReq.setName("Updated Laptop");
        updateReq.setPrice(BigDecimal.valueOf(1099.99));
        updateReq.setStockQuantity(5);
        updateReq.setDescription("Updated description");

        ProductResponse response = productService.updateProduct(1L, updateReq);

        assertNotNull(response);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void deleteProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        doNothing().when(productRepository).delete(mockProduct);

        assertDoesNotThrow(() -> productService.deleteProduct(1L));
        verify(productRepository, times(1)).delete(mockProduct);
    }

    @Test
    void deleteProduct_ThrowsResourceNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(99L));
        verify(productRepository, never()).delete(any(Product.class));
    }
}
