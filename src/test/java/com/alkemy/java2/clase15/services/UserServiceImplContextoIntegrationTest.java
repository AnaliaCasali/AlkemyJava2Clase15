package com.alkemy.java2.clase15.services;


import com.alkemy.java2.clase15.dto.UserDTO;
import com.alkemy.java2.clase15.repository.UserRepository;
import com.alkemy.java2.clase15.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceImplContextoIntegrationTest {

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  static String createdUserId;

  @Test
  @Order(1)
  void createUser_shouldPersistUser() {
    UserDTO userDTO = UserDTO.builder()
        .name("User Test")
        .username("usertest@gmail.com")
        .password("12345678")
        .roles(Set.of("USER"))
        .build();

    UserDTO savedUser = userService.createUser(userDTO);
    createdUserId = savedUser.getId();

    assertThat(savedUser.getId()).isNotBlank();
    assertThat(savedUser.getUsername()).isEqualTo("usertest@gmail.com");
    assertThat(userRepository.existsUserByUsername("usertest@gmail.com")).isTrue();
  }

  @Test
  @Order(2)
  void getAllUsers_shouldReturnListWithAtLeastOneUser() {
    List<UserDTO> users = userService.getAllUsers();
    assertThat(users).isNotEmpty();
  }

  @Test
  @Order(3)
  void updateUser_shouldModifyUser() {
    // Asegurarnos de que el usuario existe primero
    if (createdUserId == null) {
      createUser_shouldPersistUser();
    }

    UserDTO updateDTO = UserDTO.builder()
        .name("user Updated")
        .username("usertest@gmail.com")
        .password("newpassword")
        .roles(Set.of("USER"))
        .build();

    UserDTO updated = userService.updateUser(createdUserId, updateDTO);

    assertThat(updated.getName()).isEqualTo("user Updated");
  }

  @Test
  @Order(4)
  void deleteUser_shouldRemoveUser() {
    // Asegurarnos de que el usuario existe primero
    if (createdUserId == null) {
      createUser_shouldPersistUser();
    }

    userService.deleteUser(createdUserId);
    assertThat(userRepository.findById(createdUserId)).isEmpty();
  }

  @Test
  @Order(5)
  void deleteUser_shouldThrowWhenUserNotFound() {
    assertThatThrownBy(() -> userService.deleteUser("nonexistentId"))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("User not found with id");
  }
}