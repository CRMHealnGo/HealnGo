package com.example.ApiRound.crm.hyeonah.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "chat_thread",
        indexes = {
        @Index(name = "ix_thread_user_last", columnList = "user_id,last_msg_at DESC"),
        @Index(name = "ix_thread_company_last", columnList = "company_id,last_msg_at DESC"),
        @Index(name = "ix_thread_item", columnList = "item_id")}
)
public class ChatTread {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "thread_id")
    private Long threadId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "company_id", nullable = false)
    private Integer companyId;

    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "is_muted", nullable = false)
    @ColumnDefault("0")
    private boolean isMuted;

    @Column(name = "last_msg_at")
    private LocalDateTime lastMsgAt;

    @Column(name = "created_at")
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate(){
        var now = LocalDateTime.now();
        if(createAt == null) createAt = now;
        if(lastMsgAt == null) lastMsgAt = now;
        if(updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    void onUpdate() { updatedAt = LocalDateTime.now(); }

}
