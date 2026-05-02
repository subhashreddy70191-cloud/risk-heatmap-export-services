package com.riskheatmap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.riskheatmap.dto.AuthRequest;
import com.riskheatmap.dto.RefreshRequest;
import com.riskheatmap.dto.RegisterRequest;
import com.riskheatmap.repository.UserRepository;
import com.riskheatmap.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings("null")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerUser_Success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setEmail("test@example.com");
        request.setFullName("Test User");

        UserDetails mockUserDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(mockUserDetails);
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("accessToken");
        when(jwtUtil.generateRefreshToken(any(UserDetails.class))).thenReturn("refreshToken");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("accessToken"));
    }

    @Test
    void loginUser_Success() throws Exception {
        AuthRequest request = new AuthRequest("testuser", "password");
        
        UserDetails mockUserDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(mockUserDetails);
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("accessToken");
        when(jwtUtil.generateRefreshToken(any(UserDetails.class))).thenReturn("refreshToken");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));
    }

    @Test
    void refreshToken_Success() throws Exception {
        RefreshRequest request = new RefreshRequest();
        request.setRefreshToken("validRefresh");

        UserDetails mockUserDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        when(jwtUtil.extractUsername("validRefresh")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(mockUserDetails);
        when(jwtUtil.validateToken("validRefresh", mockUserDetails)).thenReturn(true);
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("newAccessToken");

        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("newAccessToken"));
    }
}
