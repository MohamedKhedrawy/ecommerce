package com.ecommerce.ecommerce.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Inject a valid base64 encoded 256-bit key for testing
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", "4qhq8L6M+R7D9Q5y+X3z7u+W8G9b7C8A9w8Q9y+E8w8=");
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", 3600000L); // 1 hour
    }

    @Test
    void generateToken_Success() {
        when(userDetails.getUsername()).thenReturn("testuser");
        String token = jwtUtil.generateToken(userDetails);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_Success() {
        when(userDetails.getUsername()).thenReturn("testuser");
        String token = jwtUtil.generateToken(userDetails);
        
        String username = jwtUtil.extractUsername(token);
        assertEquals("testuser", username);
    }

    @Test
    void validateToken_ReturnsTrueForValidToken() {
        when(userDetails.getUsername()).thenReturn("testuser");
        String token = jwtUtil.generateToken(userDetails);
        
        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void extractUsername_ThrowsSignatureException_ForInvalidToken() {
        String invalidToken = "eyJhbGciOiJIUzI1NiJ9.invalid.token";
        
        assertThrows(SignatureException.class, () -> jwtUtil.extractUsername(invalidToken));
    }

    @Test
    void validateToken_ThrowsExpiredJwtException_WhenTokenIsExpired() throws InterruptedException {
        when(userDetails.getUsername()).thenReturn("testuser");
        // Set expiration to 1 ms
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", 1L);
        String token = jwtUtil.generateToken(userDetails);
        
        // Wait for token to expire
        Thread.sleep(10);
        
        assertThrows(ExpiredJwtException.class, () -> jwtUtil.validateToken(token, userDetails));
    }
}
