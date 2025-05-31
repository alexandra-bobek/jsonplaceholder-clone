package com.jsonplaceholder.service;

import com.jsonplaceholder.dto.JwtResponse;
import com.jsonplaceholder.dto.LoginRequest;
import com.jsonplaceholder.dto.RegisterRequest;

public interface AuthService {
    JwtResponse register(RegisterRequest request);
    JwtResponse login(LoginRequest request);
} 