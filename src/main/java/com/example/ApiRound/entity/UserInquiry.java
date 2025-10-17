package com.example.ApiRound.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_inquiry")
public class UserInquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    private Integer inquiryId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Target target = Target.ADMIN;  // ADMIN

    @Enumerated(EnumType.STRING)
    @Column(name = "reporter_type", nullable = false)
    private ReporterType reporterType;     // SOCIAL, COMPANY

    @Column(name = "reporter_id")
    private Integer reporterId;            // 통합 ID

    @Column(name = "reporter_social_id")
    private Integer reporterSocialId;      // social_users의 user_id

    @Column(name = "reporter_company_id")
    private Integer reporterCompanyId;     // company_user의 company_id

    @Column(nullable = false, length = 200)
    private String subject;

    @Lob
    @Column(nullable = false)
    private String content;

    @Lob
    @Column(name = "admin_answer")
    private String adminAnswer;            // 관리자 답변

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.OPEN;   // OPEN, ANSWERED, CLOSED

    @Column(name = "assigned_to")
    private Integer assignedTo;            // 담당자 관리자 ID

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    @Column(name = "answered_by")
    private Integer answeredBy;            // 답변한 관리자 ID

    public enum Target {
        ADMIN
    }

    public enum ReporterType {
        SOCIAL, COMPANY
    }

    public enum Status {
        OPEN, ANSWERED, CLOSED
    }
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (target == null) {
            target = Target.ADMIN;
        }
        if (status == null) {
            status = Status.OPEN;
        }
    }
}
