package com.example.ApiRound.crm.hyeonah.review;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserReviewReplyRepository extends JpaRepository<UserReviewReply, Integer> {
    
    // 특정 리뷰의 답글 목록 조회
    List<UserReviewReply> findByReviewIdAndIsPublicTrueOrderByCreatedAtAsc(Integer reviewId);
    
    // 특정 리뷰의 모든 답글 조회 (공개 여부 무관)
    List<UserReviewReply> findByReviewIdOrderByCreatedAtAsc(Integer reviewId);
    
    // 특정 리뷰에 특정 업체의 답글이 있는지 확인
    boolean existsByReviewIdAndCompanyId(Integer reviewId, Integer companyId);
    
    // 특정 리뷰에 특정 업체의 답글 조회
    Optional<UserReviewReply> findByReviewIdAndCompanyId(Integer reviewId, Integer companyId);
    
    // 특정 업체의 답글 목록 조회
    List<UserReviewReply> findByCompanyIdOrderByCreatedAtDesc(Integer companyId);
    
    // 특정 리뷰의 답글 개수
    Long countByReviewId(Integer reviewId);
}

