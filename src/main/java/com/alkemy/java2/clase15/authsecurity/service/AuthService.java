package com.alkemy.java2.clase15.authsecurity.service;


import com.alkemy.java2.clase15.authsecurity.dto.AuthRequest;
import com.alkemy.java2.clase15.authsecurity.dto.AuthResponse;
import com.alkemy.java2.clase15.dto.UserDTO;

public interface AuthService {
    AuthResponse register(UserDTO request);
    AuthResponse authenticate(AuthRequest request);
}