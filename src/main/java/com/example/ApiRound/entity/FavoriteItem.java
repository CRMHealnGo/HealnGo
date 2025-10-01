package com.example.ApiRound.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "favorite_items")
@Data
public class FavoriteItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "item_id", nullable = false)
    private Long itemId;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Constructors
    public FavoriteItem() {}
    
    public FavoriteItem(Long userId, Long itemId) {
        this.userId = userId;
        this.itemId = itemId;
        this.createdAt = LocalDateTime.now();
    }
    
}
