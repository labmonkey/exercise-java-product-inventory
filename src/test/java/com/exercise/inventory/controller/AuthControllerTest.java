package com.exercise.inventory.controller;

import com.exercise.inventory.dto.AuthRequestDto;
import com.exercise.inventory.model.Role;
import com.exercise.inventory.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void login_WithValidCredentials_ShouldReturnToken() throws Exception {
        // Arrange
        AuthRequestDto authRequest = new AuthRequestDto("user", "user");
        UserDetails userDetails = new User("user", "user", Collections.singletonList(new SimpleGrantedAuthority(Role.ROLE_FULL.name())));
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("test-jwt-token");

        // Act & Assert
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(authRequest))).andExpect(status().isOk()).andExpect(jsonPath("$.token").value("test-jwt-token"));
    }

    @Test
    void login_WithNonExistentUser_ShouldReturnUnauthorized() throws Exception {
        // Arrange
        AuthRequestDto authRequest = new AuthRequestDto("nonexistent", "password");

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(authRequest))).andExpect(status().isForbidden());
    }

    @Test
    void login_WithInvalidPassword_ShouldReturnUnauthorized() throws Exception {
        // Arrange
        AuthRequestDto authRequest = new AuthRequestDto("admin", "wrongpassword");

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(authRequest))).andExpect(status().isForbidden());
    }

    @Test
    void login_WithWrongUsername_ShouldReturnUnauthorized() throws Exception {
        // Arrange
        AuthRequestDto authRequest = new AuthRequestDto("wrongUsername", "admin");

        when(authenticationManager.authenticate(any())).thenThrow(new UsernameNotFoundException("Bad credentials"));

        // Act & Assert
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(authRequest))).andExpect(status().isForbidden());
    }

    @Test
    void request_WithInvalidJwtToken_ShouldReturnUnauthorized() throws Exception {
        // Arrange
        String invalidToken = "invalid.jwt.token";

        // Act & Assert
        mockMvc.perform(get("/products").header("Authorization", "Bearer " + invalidToken)).andExpect(status().isForbidden());
    }
}
