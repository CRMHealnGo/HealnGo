package com.example.ApiRound.crm.hyeonah.review;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/review")
public class UserReviewController {
    
    private final UserReviewService userReviewService;
    private final UserReviewReplyService userReviewReplyService;
    
    // ========== 페이지 ==========
    
    @GetMapping
    public String reviewPage(Model model) {
        return "crm/review";
    }
    
    // ========== 리뷰 API ==========
    
    // 리뷰 작성
    @PostMapping
    @ResponseBody
    public ResponseEntity<UserReview> createReview(
            @RequestParam Integer userId,
            @RequestParam Long itemId,
            @RequestParam Integer bookingId,
            @RequestParam Byte rating,
            @RequestParam String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam(defaultValue = "true") Boolean isPublic) {
        
        UserReview review = new UserReview();
        review.setUserId(userId);
        review.setItemId(itemId);
        review.setBookingId(bookingId);
        review.setRating(rating);
        review.setTitle(title);
        review.setContent(content);
        review.setIsPublic(isPublic);
        
        UserReview createdReview = userReviewService.createReview(review, image);
        return ResponseEntity.ok(createdReview);
    }
    
    // 리뷰 수정
    @PutMapping("/{reviewId}")
    @ResponseBody
    public ResponseEntity<UserReview> updateReview(
            @PathVariable Integer reviewId,
            @RequestParam Byte rating,
            @RequestParam String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam(defaultValue = "true") Boolean isPublic) {
        
        UserReview review = new UserReview();
        review.setRating(rating);
        review.setTitle(title);
        review.setContent(content);
        review.setIsPublic(isPublic);
        
        UserReview updatedReview = userReviewService.updateReview(reviewId, review, image);
        return ResponseEntity.ok(updatedReview);
    }
    
    // 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    @ResponseBody
    public ResponseEntity<Void> deleteReview(@PathVariable Integer reviewId) {
        userReviewService.deleteReview(reviewId);
        return ResponseEntity.ok().build();
    }
    
    // 리뷰 상세 조회
    @GetMapping("/{reviewId}")
    @ResponseBody
    public ResponseEntity<UserReviewDto> getReview(@PathVariable Integer reviewId) {
        UserReviewDto review = userReviewService.getReviewById(reviewId);
        return ResponseEntity.ok(review);
    }
    
    // 아이템별 리뷰 목록 조회
    @GetMapping("/item/{itemId}")
    @ResponseBody
    public ResponseEntity<List<UserReviewDto>> getReviewsByItem(@PathVariable Long itemId) {
        List<UserReviewDto> reviews = userReviewService.getReviewsByItemId(itemId);
        return ResponseEntity.ok(reviews);
    }
    
    // 사용자별 리뷰 목록 조회
    @GetMapping("/user/{userId}")
    @ResponseBody
    public ResponseEntity<List<UserReviewDto>> getReviewsByUser(@PathVariable Integer userId) {
        List<UserReviewDto> reviews = userReviewService.getReviewsByUserId(userId);
        return ResponseEntity.ok(reviews);
    }
    
    // 예약 ID로 리뷰 조회
    @GetMapping("/booking/{bookingId}")
    @ResponseBody
    public ResponseEntity<UserReview> getReviewByBooking(@PathVariable Integer bookingId) {
        UserReview review = userReviewService.getReviewByBookingId(bookingId);
        if (review != null) {
            return ResponseEntity.ok(review);
        }
        return ResponseEntity.notFound().build();
    }
    
    // 리뷰 작성 가능 여부 확인
    @GetMapping("/booking/{bookingId}/can-write")
    @ResponseBody
    public ResponseEntity<Boolean> canWriteReview(@PathVariable Integer bookingId) {
        boolean canWrite = userReviewService.canWriteReview(bookingId);
        return ResponseEntity.ok(canWrite);
    }
    
    // 아이템 평균 평점 조회
    @GetMapping("/item/{itemId}/average-rating")
    @ResponseBody
    public ResponseEntity<Double> getAverageRating(@PathVariable Long itemId) {
        Double average = userReviewService.getAverageRatingByItemId(itemId);
        return ResponseEntity.ok(average);
    }
    
    // 아이템 리뷰 개수 조회
    @GetMapping("/item/{itemId}/count")
    @ResponseBody
    public ResponseEntity<Long> getReviewCount(@PathVariable Long itemId) {
        Long count = userReviewService.getReviewCountByItemId(itemId);
        return ResponseEntity.ok(count);
    }
    
    // 아이템 평점별 통계 조회
    @GetMapping("/item/{itemId}/rating-stats")
    @ResponseBody
    public ResponseEntity<Map<Byte, Long>> getRatingStats(@PathVariable Long itemId) {
        Map<Byte, Long> stats = userReviewService.getRatingStatsByItemId(itemId);
        return ResponseEntity.ok(stats);
    }
    
    // 리뷰 이미지 조회
    @GetMapping("/image/{reviewId}")
    public ResponseEntity<byte[]> getReviewImage(@PathVariable Integer reviewId) {
        try {
            byte[] imageData = userReviewService.getReviewImage(reviewId);
            if (imageData == null) {
                return ResponseEntity.notFound().build();
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); // 실제로는 imageMime을 확인해야 함
            return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 리뷰 공개/비공개 설정
    @PatchMapping("/{reviewId}/toggle-visibility")
    @ResponseBody
    public ResponseEntity<Void> toggleReviewVisibility(@PathVariable Integer reviewId) {
        userReviewService.toggleReviewVisibility(reviewId);
        return ResponseEntity.ok().build();
    }
    
    // ========== 답글 API ==========
    
    // 답글 작성
    @PostMapping("/{reviewId}/reply")
    @ResponseBody
    public ResponseEntity<UserReviewReply> createReply(
            @PathVariable Integer reviewId,
            @RequestParam Integer companyId,
            @RequestParam String body,
            @RequestParam(defaultValue = "true") Boolean isPublic) {
        
        UserReviewReply reply = new UserReviewReply();
        reply.setReviewId(reviewId);
        reply.setCompanyId(companyId);
        reply.setBody(body);
        reply.setIsPublic(isPublic);
        
        UserReviewReply createdReply = userReviewReplyService.createReply(reply);
        return ResponseEntity.ok(createdReply);
    }
    
    // 답글 수정
    @PutMapping("/reply/{replyId}")
    @ResponseBody
    public ResponseEntity<UserReviewReply> updateReply(
            @PathVariable Integer replyId,
            @RequestParam String body) {
        
        UserReviewReply updatedReply = userReviewReplyService.updateReply(replyId, body);
        return ResponseEntity.ok(updatedReply);
    }
    
    // 답글 삭제
    @DeleteMapping("/reply/{replyId}")
    @ResponseBody
    public ResponseEntity<Void> deleteReply(@PathVariable Integer replyId) {
        userReviewReplyService.deleteReply(replyId);
        return ResponseEntity.ok().build();
    }
    
    // 특정 리뷰의 답글 목록 조회
    @GetMapping("/{reviewId}/replies")
    @ResponseBody
    public ResponseEntity<List<UserReviewReplyDto>> getRepliesByReview(@PathVariable Integer reviewId) {
        List<UserReviewReplyDto> replies = userReviewReplyService.getRepliesByReviewId(reviewId);
        return ResponseEntity.ok(replies);
    }
    
    // 특정 업체의 답글 목록 조회
    @GetMapping("/company/{companyId}/replies")
    @ResponseBody
    public ResponseEntity<List<UserReviewReplyDto>> getRepliesByCompany(@PathVariable Integer companyId) {
        List<UserReviewReplyDto> replies = userReviewReplyService.getRepliesByCompanyId(companyId);
        return ResponseEntity.ok(replies);
    }
    
    // 업체가 특정 리뷰에 답글을 작성했는지 확인
    @GetMapping("/{reviewId}/company/{companyId}/has-replied")
    @ResponseBody
    public ResponseEntity<Boolean> hasCompanyReplied(
            @PathVariable Integer reviewId,
            @PathVariable Integer companyId) {
        boolean hasReplied = userReviewReplyService.hasCompanyReplied(reviewId, companyId);
        return ResponseEntity.ok(hasReplied);
    }
    
    // 답글 공개/비공개 설정
    @PatchMapping("/reply/{replyId}/toggle-visibility")
    @ResponseBody
    public ResponseEntity<Void> toggleReplyVisibility(@PathVariable Integer replyId) {
        userReviewReplyService.toggleReplyVisibility(replyId);
        return ResponseEntity.ok().build();
    }
}

