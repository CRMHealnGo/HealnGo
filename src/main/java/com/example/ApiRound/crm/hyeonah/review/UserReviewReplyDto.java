package com.example.ApiRound.crm.hyeonah.review;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserReviewReplyDto {
    
    private Integer replyId;
    private Integer reviewId;
    private Integer companyId;
    private String companyName; // 업체 이름
    private String body;
    private Boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

