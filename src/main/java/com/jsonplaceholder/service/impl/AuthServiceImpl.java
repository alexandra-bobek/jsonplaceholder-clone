package com.jsonplaceholder.service.impl;

import com.jsonplaceholder.dto.JwtResponse;
import com.jsonplaceholder.dto.LoginRequest;
import com.jsonplaceholder.dto.RegisterRequest;
import com.jsonplaceholder.model.AuthUser;
import com.jsonplaceholder.model.User;
import com.jsonplaceholder.repository.AuthUserRepository;
import com.jsonplaceholder.repository.UserRepository;
import com.jsonplaceholder.security.JwtTokenProvider;
import com.jsonplaceholder.service.AuthService;
import jakarta.persistence.EntityExistsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final AuthUserRepository authUserRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            AuthUserRepository authUserRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.authUserRepository = authUserRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    @Transactional
    public JwtResponse register(RegisterRequest request) {
        if (authUserRepository.existsByEmail(request.getEmail())) {
            throw new EntityExistsException("Email is already taken!");
        }

        // Create user
        User user = new User();
        user.setName(request.getName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user = userRepository.save(user);

        // Create auth user
        AuthUser authUser = new AuthUser();
        authUser.setName(request.getName());
        authUser.setEmail(request.getEmail());
        authUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        authUser.setUser(user);
        authUser = authUserRepository.save(authUser);

        // Generate token
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        return new JwtResponse(
            jwt,
            new JwtResponse.UserInfo(
                authUser.getId(),
                authUser.getName(),
                authUser.getUser().getUsername(),
                authUser.getEmail()
            )
        );
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        AuthUser authUser = authUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new JwtResponse(
            jwt,
            new JwtResponse.UserInfo(
                authUser.getId(),
                authUser.getName(),
                authUser.getUser().getUsername(),
                authUser.getEmail()
            )
        );
    }
} 