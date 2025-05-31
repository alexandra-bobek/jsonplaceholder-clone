package com.jsonplaceholder.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "auth_users")
public class AuthUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INTEGER")
    private Long id;
    
    private String name;
    private String email;
    private String passwordHash;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
} 