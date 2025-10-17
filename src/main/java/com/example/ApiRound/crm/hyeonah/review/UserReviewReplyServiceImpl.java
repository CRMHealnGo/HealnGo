package com.example.ApiRound.crm.hyeonah.review;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ApiRound.entity.ItemList;
import com.example.ApiRound.repository.ItemListRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserReviewReplyServiceImpl implements UserReviewReplyService {
    
    private final UserReviewReplyRepository userReviewReplyRepository;
    private final UserReviewRepository userReviewRepository;
    private final ItemListRepository itemListRepository;
    
    @Override
    public UserReviewReply createReply(UserReviewReply reply) {
        // 리뷰 조회
        UserReview review = userReviewRepository.findById(reply.getReviewId())
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));
        
        // 아이템 조회
        ItemList item = itemListRepository.findById(review.getItemId())
                .orElseThrow(() -> new RuntimeException("해당 아이템을 찾을 수 없습니다."));
        
        // 권한 검증: 아이템 소유 업체만 답글 작성 가능
        if (item.getOwnerCompany() == null || 
            !item.getOwnerCompany().getCompanyId().equals(reply.getCompanyId())) {
            throw new RuntimeException("해당 아이템의 소유 업체만 답글을 작성할 수 있습니다.");
        }
        
        // 이미 답글을 작성했는지 확인
        if (userReviewReplyRepository.existsByReviewIdAndCompanyId(reply.getReviewId(), reply.getCompanyId())) {
            throw new RuntimeException("이미 해당 리뷰에 답글을 작성하셨습니다.");
        }
        
        return userReviewReplyRepository.save(reply);
    }
    
    @Override
    public UserReviewReply updateReply(Integer replyId, String body) {
        UserReviewReply reply = userReviewReplyRepository.findById(replyId)
                .orElseThrow(() -> new RuntimeException("답글을 찾을 수 없습니다."));
        
        reply.setBody(body);
        return userReviewReplyRepository.save(reply);
    }
    
    @Override
    public void deleteReply(Integer replyId) {
        if (!userReviewReplyRepository.existsById(replyId)) {
            throw new RuntimeException("답글을 찾을 수 없습니다.");
        }
        userReviewReplyRepository.deleteById(replyId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserReviewReply getReplyById(Integer replyId) {
        return userReviewReplyRepository.findById(replyId)
                .orElseThrow(() -> new RuntimeException("답글을 찾을 수 없습니다."));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserReviewReplyDto> getRepliesByReviewId(Integer reviewId) {
        List<UserReviewReply> replies = userReviewReplyRepository.findByReviewIdAndIsPublicTrueOrderByCreatedAtAsc(reviewId);
        return replies.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserReviewReplyDto> getRepliesByCompanyId(Integer companyId) {
        List<UserReviewReply> replies = userReviewReplyRepository.findByCompanyIdOrderByCreatedAtDesc(companyId);
        return replies.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasCompanyReplied(Integer reviewId, Integer companyId) {
        return userReviewReplyRepository.existsByReviewIdAndCompanyId(reviewId, companyId);
    }
    
    @Override
    public void toggleReplyVisibility(Integer replyId) {
        UserReviewReply reply = userReviewReplyRepository.findById(replyId)
                .orElseThrow(() -> new RuntimeException("답글을 찾을 수 없습니다."));
        reply.setIsPublic(!reply.getIsPublic());
        userReviewReplyRepository.save(reply);
    }
    
    private UserReviewReplyDto convertToDto(UserReviewReply reply) {
        UserReviewReplyDto dto = new UserReviewReplyDto();
        dto.setReplyId(reply.getReplyId());
        dto.setReviewId(reply.getReviewId());
        dto.setCompanyId(reply.getCompanyId());
        dto.setBody(reply.getBody());
        dto.setIsPublic(reply.getIsPublic());
        dto.setCreatedAt(reply.getCreatedAt());
        dto.setUpdatedAt(reply.getUpdatedAt());
        return dto;
    }
}

