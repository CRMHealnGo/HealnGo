package com.example.ApiRound.crm.hyeonah.review;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserReviewRepository extends JpaRepository<UserReview, Integer> {
    
    // 특정 아이템의 리뷰 목록 조회
    List<UserReview> findByItemIdAndIsPublicTrueOrderByCreatedAtDesc(Long itemId);
    
    // 특정 사용자의 리뷰 목록 조회
    List<UserReview> findByUserIdOrderByCreatedAtDesc(Integer userId);
    
    // booking_id로 리뷰 조회
    UserReview findByBookingId(Long bookingId);
    
    // 특정 아이템의 평균 평점 조회
    @Query("SELECT AVG(r.rating) FROM UserReview r WHERE r.itemId = :itemId AND r.isPublic = true")
    Double findAverageRatingByItemId(@Param("itemId") Long itemId);
    
    // 특정 아이템의 리뷰 개수 조회
    Long countByItemIdAndIsPublicTrue(Long itemId);
    
    // 특정 평점의 리뷰 개수 조회
    @Query("SELECT COUNT(r) FROM UserReview r WHERE r.itemId = :itemId AND r.rating = :rating AND r.isPublic = true")
    Long countByItemIdAndRating(@Param("itemId") Long itemId, @Param("rating") Byte rating);
    
    // booking_id로 리뷰 존재 여부 확인
    boolean existsByBookingId(Long bookingId);
}

