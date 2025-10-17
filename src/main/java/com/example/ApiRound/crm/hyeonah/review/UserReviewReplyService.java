package com.example.ApiRound.crm.hyeonah.review;

import java.util.List;

public interface UserReviewReplyService {
    
    // 답글 작성
    UserReviewReply createReply(UserReviewReply reply);
    
    // 답글 수정
    UserReviewReply updateReply(Integer replyId, String body);
    
    // 답글 삭제
    void deleteReply(Integer replyId);
    
    // 답글 상세 조회
    UserReviewReply getReplyById(Integer replyId);
    
    // 특정 리뷰의 답글 목록 조회
    List<UserReviewReplyDto> getRepliesByReviewId(Integer reviewId);
    
    // 특정 업체의 답글 목록 조회
    List<UserReviewReplyDto> getRepliesByCompanyId(Integer companyId);
    
    // 특정 리뷰에 특정 업체가 답글을 작성했는지 확인
    boolean hasCompanyReplied(Integer reviewId, Integer companyId);
    
    // 답글 공개/비공개 설정
    void toggleReplyVisibility(Integer replyId);
}

