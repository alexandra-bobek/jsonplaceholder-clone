package com.jsonplaceholder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private UserInfo user;

    @Data
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String name;
        private String username;
        private String email;
    }

    public JwtResponse(String token, UserInfo user) {
        this.token = token;
        this.user = user;
    }
} 