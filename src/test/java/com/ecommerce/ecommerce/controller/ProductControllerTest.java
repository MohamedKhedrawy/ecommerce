package com.ecommerce.ecommerce.controller;

import com.ecommerce.ecommerce.dto.request.ProductRequest;
import com.ecommerce.ecommerce.dto.response.ProductResponse;
import com.ecommerce.ecommerce.security.JwtAuthenticationFilter;
import com.ecommerce.ecommerce.security.JwtUtil;
import com.ecommerce.ecommerce.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc 
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ProductService productService;
    
    @MockitoBean
    private JwtUtil jwtUtil; 

    @MockitoBean
    private UserDetailsService userDetailsService;

    private ProductResponse productResponse;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        productResponse = new ProductResponse();
        productResponse.setId(1L);
        productResponse.setName("Test Product");
        productResponse.setPrice(BigDecimal.valueOf(19.99));
        productResponse.setStockQuantity(100);

        productRequest = new ProductRequest();
        productRequest.setName("Test Product");
        productRequest.setPrice(BigDecimal.valueOf(19.99));
        productRequest.setStockQuantity(100);
    }

    @Test
    @WithMockUser
    void getAllProducts_ReturnsOk() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of(productResponse));

        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_AsAdmin_ReturnsCreated() throws Exception {
        when(productService.createProduct(any(ProductRequest.class))).thenReturn(productResponse);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_WithInvalidData_ReturnsBadRequest() throws Exception {
        productRequest.setPrice(BigDecimal.valueOf(-10)); // Invalid price

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }
}
