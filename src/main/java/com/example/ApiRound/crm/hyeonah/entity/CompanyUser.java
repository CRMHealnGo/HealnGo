package com.example.ApiRound.crm.hyeonah.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "company_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyUser {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private Integer companyId;
    
    @Column(name = "email", length = 190, unique = true, nullable = false)
    private String email;
    
    @Column(name = "password_hash", length = 255)
    private String passwordHash;
    
    @Column(name = "company_name", length = 150)
    private String companyName;
    
    @Column(name = "biz_no", length = 40)
    private String bizNo;
    
    @Column(name = "phone", length = 30)
    private String phone;
    
    @Column(name = "address", length = 255)
    private String address;
    
    @Lob
    @Column(name = "avatar_blob", columnDefinition = "LONGBLOB")
    private byte[] avatarBlob;
    
    @Column(name = "avatar_mime", length = 50)
    private String avatarMime;
    
    @Column(name = "avatar_updated_at")
    private LocalDateTime avatarUpdatedAt;
    
    @Column(name = "is_active", columnDefinition = "TINYINT(1) DEFAULT 1")
    @Builder.Default
    private Boolean isActive = true;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.isActive == null) {
            this.isActive = true;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
