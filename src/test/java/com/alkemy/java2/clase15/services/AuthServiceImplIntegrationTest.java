package com.alkemy.java2.clase15.services;


import com.alkemy.java2.clase15.authsecurity.dto.AuthRequest;
import com.alkemy.java2.clase15.authsecurity.dto.AuthResponse;
import com.alkemy.java2.clase15.authsecurity.service.AuthService;
import com.alkemy.java2.clase15.dto.UserDTO;
import com.alkemy.java2.clase15.repository.UserRepository;
import org.junit.jupiter.api.*;
    import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Testcontainers
class AuthServiceImplIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
      registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }


  @Autowired
  private AuthService authService;

  @Autowired
  private UserRepository userRepository;

  @Test
  void register_shouldCreateUserSuccessfully() {
    UserDTO userDTO = UserDTO.builder()
        .username("integration@test.com")
        .password("securePassword")
        .roles(Set.of("USER"))
        .build();

    AuthResponse response = authService.register(userDTO);

    assertThat(response.getToken()).isNotBlank();
    assertThat(userRepository.existsUserByUsername("integration@test.com")).isTrue();
  }

  @Test
  void authenticate_shouldReturnTokenForValidUser() {
    AuthRequest request = AuthRequest.builder()
        .username("integration@test.com")
        .password("securePassword")
        .build();

    AuthResponse response = authService.authenticate(request);

    assertThat(response.getToken()).isNotBlank();
  }

  @Test
  void authenticate_shouldThrowForInvalidPassword() {
    AuthRequest request = AuthRequest.builder()
        .username("integration@test.com")
        .password("wrongPassword")
        .build();

    assertThatThrownBy(() -> authService.authenticate(request))
        .isInstanceOf(BadCredentialsException.class)
        .hasMessage("Invalid credentials");
  }

  @Test
  void authenticate_shouldThrowForNonExistentUser() {
    AuthRequest request = AuthRequest.builder()
        .username("nonexistent@test.com")
        .password("irrelevant")
        .build();

    assertThatThrownBy(() -> authService.authenticate(request))
        .isInstanceOf(BadCredentialsException.class)
        .hasMessage("Invalid credentials");
  }
}

