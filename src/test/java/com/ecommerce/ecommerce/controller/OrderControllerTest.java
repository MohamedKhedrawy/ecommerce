package com.ecommerce.ecommerce.controller;

import com.ecommerce.ecommerce.dto.request.OrderRequest;
import com.ecommerce.ecommerce.dto.response.OrderResponse;
import com.ecommerce.ecommerce.security.JwtUtil;
import com.ecommerce.ecommerce.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private OrderRequest orderRequest;
    private OrderResponse orderResponse;

    @BeforeEach
    void setUp() {
        OrderRequest.OrderItemRequest itemReq = new OrderRequest.OrderItemRequest();
        itemReq.setProductId(1L);
        itemReq.setQuantity(2);

        orderRequest = new OrderRequest();
        orderRequest.setItems(List.of(itemReq));

        orderResponse = new OrderResponse();
        orderResponse.setId(100L);
        orderResponse.setTotalAmount(java.math.BigDecimal.valueOf(19.99));
    }

    @Test
    @WithMockUser(username = "testuser")
    void placeOrder_ReturnsCreated() throws Exception {
        when(orderService.placeOrder(any(OrderRequest.class), anyString())).thenReturn(orderResponse);

        mockMvc.perform(post("/api/orders")
                .principal(() -> "testuser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L));
    }

    @Test
    @WithMockUser
    void placeOrder_WithEmptyItems_ReturnsBadRequest() throws Exception {
        orderRequest.setItems(List.of()); // Invalid: empty items

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }
}
