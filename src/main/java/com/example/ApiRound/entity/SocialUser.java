package com.example.ApiRound.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "social_user")
@Data
public class SocialUser {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "provider", nullable = false)
    private String provider;
    
    @Column(name = "provider_id", nullable = false)
    private String providerId;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "profile_image")
    private String profileImage;
    
    @Column(name = "access_token")
    private String accessToken;
    
    @Column(name = "refresh_token")
    private String refreshToken;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public SocialUser() {}
    
    public SocialUser(String provider, String providerId, String name, String email, String profileImage) {
        this.provider = provider;
        this.providerId = providerId;
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
}
