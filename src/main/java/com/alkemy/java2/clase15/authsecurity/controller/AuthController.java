package com.alkemy.java2.clase15.authsecurity.controller;

import com.alkemy.java2.clase15.authsecurity.dto.AuthRequest;
import com.alkemy.java2.clase15.authsecurity.dto.AuthResponse;
import com.alkemy.java2.clase15.authsecurity.service.AuthService;
import com.alkemy.java2.clase15.dto.UserDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;
  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserDTO request) {
    return ResponseEntity.ok(authService.register(request));}
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
    return ResponseEntity.ok(authService.authenticate(request));   }}
