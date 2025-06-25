package com.alkemy.java2.clase15.controller;

import com.alkemy.java2.clase15.authsecurity.dto.AuthRequest;
import com.alkemy.java2.clase15.dto.UserDTO;
import com.alkemy.java2.clase15.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc

@Testcontainers
class AuthControllerIntegrationTest {

  @Container
  static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
  }

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
  }

  @Test
  void registerUser_shouldReturnJwtToken_whenRegistrationDataIsValid() throws Exception {
    // Arrange
    UserDTO validUserRequest = UserDTO.builder()
        .name("Test User")
        .username("test@example.com")
        .password("securePassword123")
        .roles(Set.of("USER"))
        .build();

    // Act & Assert
    mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validUserRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").exists())
        .andExpect(jsonPath("$.token").isString())
        .andExpect(jsonPath("$.token").isNotEmpty());
  }

  @Test
  void authenticateUser_shouldReturnJwtToken_whenCredentialsAreValid() throws Exception {
    // Arrange - Register a test user first
    UserDTO testUser = UserDTO.builder()
        .name("Auth Test User")
        .username("auth@test.com")
        .password("testPassword")
        .roles(Set.of("USER"))
        .build();

    mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(testUser)));

    AuthRequest validCredentials = AuthRequest.builder()
        .username("auth@test.com")
        .password("testPassword")
        .build();

    // Act & Assert
    mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validCredentials)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").exists())
        .andExpect(jsonPath("$.token").isString())
        .andExpect(jsonPath("$.token").isNotEmpty());
  }
  @Test
  void authenticateUser_shouldReturnBadRequestStatus_whenCredentialsAreInvalid() throws Exception {
    // Arrange
    AuthRequest invalidCredentials = AuthRequest.builder()
        .username("nonexistent@user.com")
        .password("wrongPassword123")
        .build();

    // Act & Assert
    mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidCredentials)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Unexpected error: Invalid credentials"));
  }

  @Test
  void registerUser_shouldReturnBadRequest_whenUsernameAlreadyExists() throws Exception {
    // Arrange - First register a user
    UserDTO initialUser = UserDTO.builder()
        .name("Existing User")
        .username("duplicate@test.com")
        .password("initialPass")
        .roles(Set.of("USER"))
        .build();

    mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(initialUser)));

    // Try to register same username again
    UserDTO duplicateUser = UserDTO.builder()
        .name("Duplicate User")
        .username("duplicate@test.com")  // Same username
        .password("differentPass")
        .roles(Set.of("USER"))
        .build();

    // Act & Assert
    mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(duplicateUser)))
        .andExpect(status().isBadRequest());
  }
}