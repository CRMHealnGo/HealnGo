package com.example.ApiRound.crm.hyeonah.review;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.ApiRound.repository.ItemListRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserReviewServiceImpl implements UserReviewService {
    
    private final UserReviewRepository userReviewRepository;
    private final UserReviewReplyRepository userReviewReplyRepository;
    private final ItemListRepository itemListRepository;
    
    @Override
    public UserReview createReview(UserReview review, MultipartFile image) {
        // bookingId가 설정되어 있는지 확인
        if (review.getBookingId() == null) {
            throw new RuntimeException("예약 정보가 없습니다.");
        }
        
        // 이미 리뷰가 작성되었는지 확인
        if (userReviewRepository.existsByBookingId(review.getBookingId())) {
            throw new RuntimeException("이미 해당 예약에 대한 리뷰가 작성되었습니다.");
        }
        
        // 아이템이 존재하는지 확인
        if (review.getItemId() != null && !itemListRepository.existsById(review.getItemId())) {
            throw new RuntimeException("해당 아이템을 찾을 수 없습니다.");
        }
        
        // 이미지 처리
        if (image != null && !image.isEmpty()) {
            try {
                review.setImageBlob(image.getBytes());
                review.setImageMime(image.getContentType());
            } catch (IOException e) {
                throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다.", e);
            }
        }
        
        return userReviewRepository.save(review);
    }
    
    @Override
    public UserReview updateReview(Integer reviewId, UserReview review, MultipartFile image) {
        UserReview existingReview = userReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));
        
        existingReview.setRating(review.getRating());
        existingReview.setTitle(review.getTitle());
        existingReview.setContent(review.getContent());
        existingReview.setIsPublic(review.getIsPublic());
        
        // 새 이미지가 있으면 업데이트
        if (image != null && !image.isEmpty()) {
            try {
                existingReview.setImageBlob(image.getBytes());
                existingReview.setImageMime(image.getContentType());
            } catch (IOException e) {
                throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다.", e);
            }
        }
        
        return userReviewRepository.save(existingReview);
    }
    
    @Override
    public void deleteReview(Integer reviewId) {
        if (!userReviewRepository.existsById(reviewId)) {
            throw new RuntimeException("리뷰를 찾을 수 없습니다.");
        }
        userReviewRepository.deleteById(reviewId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserReviewDto getReviewById(Integer reviewId) {
        UserReview review = userReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));
        
        return convertToDto(review);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserReviewDto> getReviewsByItemId(Long itemId) {
        List<UserReview> reviews = userReviewRepository.findByItemIdAndIsPublicTrueOrderByCreatedAtDesc(itemId);
        return reviews.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserReviewDto> getReviewsByUserId(Integer userId) {
        List<UserReview> reviews = userReviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return reviews.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserReview getReviewByReservationId(Long reservationId) {
        return userReviewRepository.findByBookingId(reservationId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean canWriteReview(Long reservationId) {
        return !userReviewRepository.existsByBookingId(reservationId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double getAverageRatingByItemId(Long itemId) {
        Double average = userReviewRepository.findAverageRatingByItemId(itemId);
        return average != null ? Math.round(average * 10) / 10.0 : 0.0;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long getReviewCountByItemId(Long itemId) {
        return userReviewRepository.countByItemIdAndIsPublicTrue(itemId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<Byte, Long> getRatingStatsByItemId(Long itemId) {
        Map<Byte, Long> stats = new HashMap<>();
        for (byte rating = 1; rating <= 5; rating++) {
            Long count = userReviewRepository.countByItemIdAndRating(itemId, rating);
            stats.put(rating, count);
        }
        return stats;
    }
    
    @Override
    @Transactional(readOnly = true)
    public byte[] getReviewImage(Integer reviewId) {
        UserReview review = userReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));
        return review.getImageBlob();
    }
    
    @Override
    public void toggleReviewVisibility(Integer reviewId) {
        UserReview review = userReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));
        review.setIsPublic(!review.getIsPublic());
        userReviewRepository.save(review);
    }
    
    // DTO 변환 헬퍼 메서드
    private UserReviewDto convertToDto(UserReview review) {
        UserReviewDto dto = new UserReviewDto();
        dto.setReviewId(review.getReviewId());
        dto.setUserId(review.getUserId());
        dto.setItemId(review.getItemId());
        dto.setBookingId(review.getBookingId());
        dto.setRating(review.getRating());
        dto.setTitle(review.getTitle());
        dto.setContent(review.getContent());
        dto.setImageMime(review.getImageMime());
        dto.setIsPublic(review.getIsPublic());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());
        
        // 이미지가 있으면 URL 설정
        if (review.getImageBlob() != null) {
            dto.setImageUrl("/review/image/" + review.getReviewId());
        }
        
        // 답글 조회
        List<UserReviewReply> replies = userReviewReplyRepository.findByReview_ReviewIdAndIsPublicTrueOrderByCreatedAtAsc(review.getReviewId());
        dto.setReplies(replies.stream()
                .map(this::convertReplyToDto)
                .collect(Collectors.toList()));
        dto.setReplyCount((long) replies.size());
        
        return dto;
    }
    
    private UserReviewReplyDto convertReplyToDto(UserReviewReply reply) {
        UserReviewReplyDto dto = new UserReviewReplyDto();
        dto.setReplyId(reply.getReplyId());
        dto.setReviewId(reply.getReview().getReviewId());
        dto.setCompanyId(reply.getCompanyId());
        dto.setBody(reply.getBody());
        dto.setIsPublic(reply.getIsPublic());
        dto.setCreatedAt(reply.getCreatedAt());
        dto.setUpdatedAt(reply.getUpdatedAt());
        return dto;
    }
}

