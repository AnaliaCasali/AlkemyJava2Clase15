package com.alkemy.java2.clase15.repository;

import com.alkemy.java2.clase15.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@Testcontainers
class UserRepositoryIntegrationTest {

  @Container
  static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
  }

  @Autowired
  private UserRepository userRepository;

  private User testUser;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
    testUser = User.builder()
        .name("usuario administrador")
        .username("testuser")
        .password("admin123")
        .build();
    userRepository.save(testUser);
  }

  // Tests existentes
  @Test
  void findByUsername_shouldReturnUser_whenUsernameExists() {
   // act
    Optional<User> foundUser = userRepository.findByUsername("testuser");
    // asert
    assertTrue(foundUser.isPresent());
    assertEquals("testuser", foundUser.get().getUsername());
  }

  @Test
  void findByUsername_shouldReturnEmptyOptional_whenUsernameDoesNotExist() {
    Optional<User> foundUser = userRepository.findByUsername("nonexistent");
   //asert
    assertFalse(foundUser.isPresent());
  }

  @Test
  void existsUserByUsername_shouldReturnTrue_whenUsernameExists() {
    boolean exists = userRepository.existsUserByUsername("testuser");
    assertTrue(exists);
  }

  @Test
  void existsUserByUsername_shouldReturnFalse_whenUsernameDoesNotExist() {
    boolean exists = userRepository.existsUserByUsername("nonexistent");
    assertFalse(exists);
  }

  // Nuevos tests CRUD
  @Test
  void save_shouldCreateNewUser() {
    // Arrange
    User newUser = User.builder()
        .name("nuevo usuario")
        .username("newuser")
        .password("newpass123")
        .build();

    // Act
    User savedUser = userRepository.save(newUser);

    // Assert
    assertNotNull(savedUser.getId());
    assertEquals("newuser", savedUser.getUsername());

    Optional<User> retrievedUser = userRepository.findById(savedUser.getId());
    assertTrue(retrievedUser.isPresent());
    assertEquals("nuevo usuario", retrievedUser.get().getName());
  }

  @Test
  void findById_shouldReturnUser_whenIdExists() {
    // Act
    Optional<User> foundUser = userRepository.findById(testUser.getId());

    // Assert
    assertTrue(foundUser.isPresent());
    assertEquals(testUser.getUsername(), foundUser.get().getUsername());
  }

  @Test
  void findById_shouldReturnEmptyOptional_whenIdDoesNotExist() {
    // Act
    Optional<User> foundUser = userRepository.findById("nonexistent-id");

    // Assert
    assertFalse(foundUser.isPresent());
  }

  @Test
  void findAll_shouldReturnAllUsers() {
    // Arrange
    User anotherUser = User.builder()
        .name("otro usuario")
        .username("anotheruser")
        .password("anotherpass")
        .build();
    userRepository.save(anotherUser);

    // Act
    List<User> users = userRepository.findAll();

    // Assert
    assertEquals(2, users.size());
    assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("testuser")));
    assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("anotheruser")));
  }

  @Test
  void update_shouldModifyExistingUser() {
    // Arrange
    testUser.setName("nombre modificado");
    testUser.setPassword("newpassword");

    // Act
    User updatedUser = userRepository.save(testUser);

    // Assert
    assertEquals(testUser.getId(), updatedUser.getId());
    assertEquals("nombre modificado", updatedUser.getName());
    assertEquals("newpassword", updatedUser.getPassword());

    Optional<User> retrievedUser = userRepository.findById(testUser.getId());
    assertTrue(retrievedUser.isPresent());
    assertEquals("nombre modificado", retrievedUser.get().getName());
  }

  @Test
  void deleteById_shouldRemoveUser() {
    // Act
    userRepository.deleteById(testUser.getId());

    // Assert
    Optional<User> deletedUser = userRepository.findById(testUser.getId());
    assertFalse(deletedUser.isPresent());
    assertEquals(0, userRepository.count());
  }

  @Test
  void count_shouldReturnNumberOfUsers() {
    // Arrange
    User anotherUser = User.builder()
        .name("otro usuario")
        .username("anotheruser")
        .password("anotherpass")
        .build();
    userRepository.save(anotherUser);

    // Act
    long count = userRepository.count();

    // Assert
    assertEquals(2, count);
  }
}