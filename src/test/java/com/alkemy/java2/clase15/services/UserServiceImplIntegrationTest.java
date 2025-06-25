package com.alkemy.java2.clase15.services;



import com.alkemy.java2.clase15.dto.UserDTO;
import com.alkemy.java2.clase15.repository.UserRepository;
import com.alkemy.java2.clase15.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

class UserServiceImplIntegrationTest {

  @Container
  static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

  @DynamicPropertySource
  static void mongoProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
  }

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  static String createdUserId;

  @Test
  @Order(1)
  void createUser_shouldPersistUser() {
    UserDTO userDTO = UserDTO.builder()
        .name("user test")
        .username("user@gmail.com")
        .password("12345678")
        .roles(Set.of("USER"))
        .build();

    UserDTO savedUser = userService.createUser(userDTO);
    createdUserId = savedUser.getId();

    assertThat(savedUser.getId()).isNotBlank();
    assertThat(savedUser.getUsername()).isEqualTo("user@gmail.com");
    assertThat(userRepository.existsUserByUsername("user@gmail.com")).isTrue();
  }

  @Test
  @Order(2)
  void getAllUsers_shouldReturnListWithAtLeastOneUser() {
    List<UserDTO> users = userService.getAllUsers();
    assertThat(users).isNotEmpty();
  }

  @Test
  @Order(4)
  void updateUser_shouldModifyUser() {
    UserDTO updateDTO = UserDTO.builder()
        .name("user Updated")
        .username("user@gmail.com")
        .password("newpassword")
        .roles(Set.of("USER"))
        .build();

    UserDTO updated = userService.updateUser(createdUserId, updateDTO);

    assertThat(updated.getName()).isEqualTo("user Updated");
  }

  @Test
  @Order(5)
  void deleteUser_shouldRemoveUser() {
    userService.deleteUser(createdUserId);
    assertThat(userRepository.findById(createdUserId)).isNotPresent();
  }

  @Test
  @Order(6)
  void deleteUser_shouldThrowWhenUserNotFound() {
    assertThatThrownBy(() -> userService.deleteUser("nonexistentId"))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("User not found with id");
  }
}
