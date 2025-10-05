package com.example.ApiRound.crm.hyeonah.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "reservations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ReservationManagement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(nullable = false)
    private LocalTime startTime;
    
    @Column(nullable = false)
    private LocalTime endTime;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(length = 200)
    private String location;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationManagementStatus status;
    
    @Column(length = 100)
    private String googleEventId; // 구글 캘린더 이벤트 ID (동기화용)
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean googleSyncEnabled = false; // 구글 동기화 활성화 여부
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // 예약 상태 열거형
    public enum ReservationManagementStatus {
        CONFIRMED,      // 확정
        PENDING,        // 대기
        CANCELLED,      // 취소
        COMPLETED       // 완료
    }
}