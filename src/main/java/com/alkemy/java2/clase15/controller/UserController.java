package com.alkemy.java2.clase15.controller;

import com.alkemy.java2.clase15.dto.UserDTO;
import com.alkemy.java2.clase15.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// user controller
@RestController
@Slf4j
@RequestMapping("/api/v1/users")
@Tag(name = "Usuarios", description = "Operaciones relacionadas con usuarios internas del sistema")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping
  @Operation(
      summary = "Obtener todos los usuarios",
      description = "Devuelve una lista con todas los usuarios del sistema"
  )
  public ResponseEntity<List<UserDTO>> obtenerTodas() {
    List<UserDTO> lista = userService.getAllUsers();
    return ResponseEntity.ok(lista);
  }

/*
  @GetMapping("/{id}")
  @Operation(
      summary = "Obtener usuario por ID",
      description = "Devuelve un usuario específico según su ID"
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
      @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
  })
  public ResponseEntity<UserDTO> obtenerPorId(@PathVariable String id) {
    return userService.getUserById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }*/

  @PostMapping
  @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente")
  public ResponseEntity<UserDTO> crear(@RequestBody UserDTO user) {
    UserDTO creado = userService.createUser(user);
    return ResponseEntity.status(HttpStatus.CREATED).body(creado);
  }

  @PutMapping("/{id}")
  @Operation(
      summary = "Actualizar usuario",
      description = "Actualiza los datos de un usuario existente"
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description
          = "Usuario actualizado exitosamente"),
      @ApiResponse(responseCode = "404",
          description = "Usuario no encontrado")
  })
  public ResponseEntity<UserDTO> actualizar(
      @PathVariable String id,
      @RequestBody UserDTO userDTO) {
    UserDTO actualizado = userService.updateUser(id, userDTO);
    return ResponseEntity.ok(actualizado);
  }

  @DeleteMapping("/{id}")
  @Operation(
      summary = "Eliminar usuario",
      description = "Elimina un usuario del sistema"
  )
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
      @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
  })
  public ResponseEntity<Void> eliminar(@PathVariable String id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }
}