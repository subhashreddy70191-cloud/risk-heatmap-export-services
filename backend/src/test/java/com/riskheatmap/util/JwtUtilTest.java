package com.riskheatmap.util;

import com.riskheatmap.config.JwtProperties;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class JwtUtilTest {

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private JwtUtil jwtUtil;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        lenient().when(jwtProperties.getSecret()).thenReturn("ThisIsAVerySecureSecretKeyForTestingJwtTokenGeneration123!!");
        lenient().when(jwtProperties.getExpirationMs()).thenReturn(3600000L); // 1 hour
        lenient().when(jwtProperties.getRefreshExpirationMs()).thenReturn(86400000L); // 24 hours

        userDetails = User.withUsername("testuser")
                .password("password")
                .authorities("ROLE_USER")
                .build();
    }

    @Test
    void generateAndValidateToken() {
        String token = jwtUtil.generateToken(userDetails);
        assertNotNull(token);

        String username = jwtUtil.extractUsername(token);
        assertEquals("testuser", username);

        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void generateRefreshToken() {
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        assertNotNull(refreshToken);

        String username = jwtUtil.extractUsername(refreshToken);
        assertEquals("testuser", username);

        Date expiration = jwtUtil.extractExpiration(refreshToken);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void extractClaim() {
        String token = jwtUtil.generateToken(new HashMap<>(), userDetails, 10000L);
        Claims claims = jwtUtil.extractClaim(token, c -> c);
        assertEquals("testuser", claims.getSubject());
    }
}
