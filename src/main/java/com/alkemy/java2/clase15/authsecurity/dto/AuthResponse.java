package com.alkemy.java2.clase15.authsecurity.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
  private String token;
}
