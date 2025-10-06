package com.example.ApiRound.crm.hyeonah.entity;

import java.time.LocalDateTime;

public class CompanyUser {
    private Integer companyId;
    private String email;
    private String passwordHash;
    private String companyName;
    private String bizNo;
    private String phone;
    private String address;
    private byte[] avatarBlob;
    private String avatarMime;
    private LocalDateTime avatarUpdatedAt;
    private Integer isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 기본 생성자
    public CompanyUser() {}

    // 전체 생성자
    public CompanyUser(Integer companyId, String email, String passwordHash, String companyName, String bizNo,
                      String phone, String address, byte[] avatarBlob, String avatarMime, LocalDateTime avatarUpdatedAt,
                      Integer isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.companyId = companyId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.companyName = companyName;
        this.bizNo = bizNo;
        this.phone = phone;
        this.address = address;
        this.avatarBlob = avatarBlob;
        this.avatarMime = avatarMime;
        this.avatarUpdatedAt = avatarUpdatedAt;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getBizNo() {
        return bizNo;
    }

    public void setBizNo(String bizNo) {
        this.bizNo = bizNo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
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
        return "CompanyUser{" +
                "companyId=" + companyId +
                ", email='" + email + '\'' +
                ", companyName='" + companyName + '\'' +
                ", bizNo='" + bizNo + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}
