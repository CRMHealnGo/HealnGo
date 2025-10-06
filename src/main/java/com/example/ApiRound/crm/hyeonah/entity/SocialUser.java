package com.example.ApiRound.crm.hyeonah.entity;

import java.time.LocalDateTime;

public class SocialUser {
    private Integer userId;
    private String email;
    private String passwordHash;
    private String name;
    private String phone;
    private byte[] avatarBlob;
    private String avatarMime;
    private LocalDateTime avatarUpdatedAt;
    private LocalDateTime lastLoginAt;
    private Integer isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 기본 생성자
    public SocialUser() {}

    // 전체 생성자
    public SocialUser(Integer userId, String email, String passwordHash, String name, String phone,
                     byte[] avatarBlob, String avatarMime, LocalDateTime avatarUpdatedAt,
                     LocalDateTime lastLoginAt, Integer isDeleted, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
        this.phone = phone;
        this.avatarBlob = avatarBlob;
        this.avatarMime = avatarMime;
        this.avatarUpdatedAt = avatarUpdatedAt;
        this.lastLoginAt = lastLoginAt;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public byte[] getAvatarBlob() {
        return avatarBlob;
    }

    public void setAvatarBlob(byte[] avatarBlob) {
        this.avatarBlob = avatarBlob;
    }

    public String getAvatarMime() {
        return avatarMime;
    }

    public void setAvatarMime(String avatarMime) {
        this.avatarMime = avatarMime;
    }

    public LocalDateTime getAvatarUpdatedAt() {
        return avatarUpdatedAt;
    }

    public void setAvatarUpdatedAt(LocalDateTime avatarUpdatedAt) {
        this.avatarUpdatedAt = avatarUpdatedAt;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "SocialUser{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", isDeleted=" + isDeleted +
                ", createdAt=" + createdAt +
                '}';
    }
}
