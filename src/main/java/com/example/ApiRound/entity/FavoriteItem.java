package com.example.ApiRound.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "favorite_items")
@IdClass(FavoriteItem.FavoriteItemId.class)
@Data
public class FavoriteItem {
    
    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Id
    @Column(name = "item_id", nullable = false)
    private Long itemId;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Constructors
    public FavoriteItem() {}
    
    public FavoriteItem(Long userId, Long itemId) {
        this.userId = userId;
        this.itemId = itemId;
    }
    
    // JPA 저장 전 자동으로 created_at 설정
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
    // 복합키 클래스
    @Data
    public static class FavoriteItemId implements Serializable {
        private Long userId;
        private Long itemId;
        
        public FavoriteItemId() {}
        
        public FavoriteItemId(Long userId, Long itemId) {
            this.userId = userId;
            this.itemId = itemId;
        }
    }
}
