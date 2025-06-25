package com.alkemy.java2.clase15.controller;

import com.alkemy.java2.clase15.dto.UserDTO;
import com.alkemy.java2.clase15.enums.Role;
import com.alkemy.java2.clase15.model.User;
import com.alkemy.java2.clase15.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class UserControllerIntegrationTest {

  @Container
  static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
  }

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ObjectMapper objectMapper;

  private User testUser;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
    testUser = User.builder()
        .name("Test User")
        .username("testuser@example.com")
        .password("encrypted-password") // usa encoder en tests reales si es necesario
        .roles(Set.of(Role.ADMIN))
        .active(true)
        .build();
    userRepository.save(testUser);
  }


  @Test
  @WithMockUser(username = "testuser@example.com", roles = {"ADMIN"})
  void obtenerTodas_shouldReturnUsersList() throws Exception {
    mockMvc.perform(get("/api/v1/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].username").value("testuser@example.com"));
  }

  @Test
  @WithMockUser(username = "testuser@example.com", roles = {"ADMIN"})
  void crear_shouldCreateUser() throws Exception {
    UserDTO newUser = UserDTO.builder()
        .name("Nuevo Usuario")
        .username("nuevo@example.com")
        .password("pass123")
        .roles(Set.of("USER"))
        .build();

    mockMvc.perform(post("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newUser)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("nuevo@example.com"));
  }

  @Test
  @WithMockUser(username = "testuser@example.com", roles = {"ADMIN"})
  void actualizar_shouldUpdateUser() throws Exception {
    UserDTO updatedUser = UserDTO.builder()
        .name("Usuario Actualizado")
        .username("testuser@example.com")
        .roles(Set.of("ADMIN"))
        .build();

    mockMvc.perform(put("/api/v1/users/" + testUser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedUser)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Usuario Actualizado"));
  }
  @Test
  @WithMockUser(username = "testuser@example.com", roles = {"ADMIN"})
  void eliminar_shouldDeleteUser() throws Exception {
    mockMvc.perform(delete("/api/v1/users/" + testUser.getId()))
        .andExpect(status().isNoContent());
  }

}
