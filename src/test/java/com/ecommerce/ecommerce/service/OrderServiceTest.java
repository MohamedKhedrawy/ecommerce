package com.ecommerce.ecommerce.service;

import com.ecommerce.ecommerce.dto.request.OrderRequest;
import com.ecommerce.ecommerce.dto.response.OrderResponse;
import com.ecommerce.ecommerce.exception.InsufficientStockException;
import com.ecommerce.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce.model.Order;
import com.ecommerce.ecommerce.model.Product;
import com.ecommerce.ecommerce.model.User;
import com.ecommerce.ecommerce.repository.OrderRepository;
import com.ecommerce.ecommerce.repository.ProductRepository;
import com.ecommerce.ecommerce.repository.UserRepository;
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
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    private User mockUser;
    private Product mockProduct;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("johndoe");

        mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setName("Test Product");
        mockProduct.setPrice(BigDecimal.valueOf(100.00));
        mockProduct.setStockQuantity(10);
    }

    @Test
    void placeOrder_Success() {
        // Arrange
        OrderRequest.OrderItemRequest itemReq = new OrderRequest.OrderItemRequest();
        itemReq.setProductId(1L);
        itemReq.setQuantity(2); // We want 2, we have 10

        OrderRequest request = new OrderRequest();
        request.setItems(List.of(itemReq));

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        
        Order mockSavedOrder = new Order();
        mockSavedOrder.setId(100L);
        mockSavedOrder.setUser(mockUser);
        // We mock save to return an order with an ID so mapToResponse doesn't fail
        when(orderRepository.save(any(Order.class))).thenReturn(mockSavedOrder);

        // Act
        OrderResponse response = orderService.placeOrder(request, "johndoe");

        // Assert
        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(8, mockProduct.getStockQuantity()); // 10 - 2 = 8
        verify(productRepository, times(1)).save(mockProduct);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void placeOrder_ThrowsInsufficientStockException() {
        // Arrange
        OrderRequest.OrderItemRequest itemReq = new OrderRequest.OrderItemRequest();
        itemReq.setProductId(1L);
        itemReq.setQuantity(20); // We want 20, we only have 10

        OrderRequest request = new OrderRequest();
        request.setItems(List.of(itemReq));

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));

        // Act & Assert
        InsufficientStockException ex = assertThrows(InsufficientStockException.class, () -> {
            orderService.placeOrder(request, "johndoe");
        });

        assertTrue(ex.getMessage().contains("Test Product"));
        verify(productRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void placeOrder_ThrowsResourceNotFoundException_WhenProductDoesNotExist() {
        // Arrange
        OrderRequest.OrderItemRequest itemReq = new OrderRequest.OrderItemRequest();
        itemReq.setProductId(99L);
        itemReq.setQuantity(1);

        OrderRequest request = new OrderRequest();
        request.setItems(List.of(itemReq));

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(mockUser));
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.placeOrder(request, "johndoe");
        });
    }
}
