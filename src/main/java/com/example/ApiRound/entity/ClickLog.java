package com.example.ApiRound.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "click_logs")
@Data
public class ClickLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "company_id", nullable = false)
    private Long companyId;
    
    @Column(name = "clicked_at")
    private LocalDateTime clickedAt;
    
    // Constructors
    public ClickLog() {}
    
    public ClickLog(Long companyId) {
        this.companyId = companyId;
        this.clickedAt = LocalDateTime.now();
    }
   
}
