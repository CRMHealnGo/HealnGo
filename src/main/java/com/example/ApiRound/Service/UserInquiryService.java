package com.example.ApiRound.Service;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ApiRound.crm.minggzz.AdminStatusRequest;
import com.example.ApiRound.dto.InquirySubmitRequest;
import com.example.ApiRound.entity.UserInquiry;
import com.example.ApiRound.entity.UserInquiry.Status;
import com.example.ApiRound.repository.UserInquiryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserInquiryService {

    private final UserInquiryRepository userInquiryRepository;

    /** 사용자 문의 접수 */
    @Transactional
    public Integer submit(InquirySubmitRequest req, Integer userId) {
        System.out.println("===== UserInquiryService.submit 시작 =====");
        System.out.println("userId: " + userId);
        System.out.println("subject: " + req.getSubject());
        System.out.println("content: " + req.getContent());
        
        UserInquiry entity = UserInquiry.builder()
                .reporterId(userId)
                .reporterSocialId(userId)  // social_users의 user_id
                .reporterType(UserInquiry.ReporterType.SOCIAL)
                .target(UserInquiry.Target.ADMIN)
                .subject(req.getSubject())
                .content(req.getContent())
                .status(Status.OPEN)
                .createdAt(LocalDateTime.now())
                .build();

        System.out.println("Entity 생성 완료, DB 저장 시작...");
        
        // TODO: 첨부 저장이 필요하다면 req.getAttachment() 처리 로직 추가

        UserInquiry saved = userInquiryRepository.save(entity);
        
        System.out.println("DB 저장 완료! inquiry_id: " + saved.getInquiryId());
        
        return saved.getInquiryId();
    }

    /** (사용자) 내 문의 페이징 조회 */
    @Transactional(readOnly = true)
    public Page<UserInquiry> getMyPagedList(Integer userId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), Math.max(size, 1),
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Example<UserInquiry> example = Example.of(
                UserInquiry.builder()
                    .reporterSocialId(userId)
                    .reporterType(UserInquiry.ReporterType.SOCIAL)
                    .build(),
                ExampleMatcher.matching().withIgnoreNullValues()
        );
        return userInquiryRepository.findAll(example, pageable);
    }

    /** (관리자) 목록 페이징 - 상태 필터 optional */
    @Transactional(readOnly = true)
    public Page<UserInquiry> getAdminPagedList(int page, int size, String statusFilter) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), Math.max(size, 1),
                Sort.by(Sort.Direction.DESC, "createdAt"));

        if (statusFilter == null || statusFilter.isBlank()) {
            return userInquiryRepository.findAll(pageable);
        }

        Status st;
        try {
            st = Status.valueOf(statusFilter.toUpperCase());
        } catch (Exception e) {
            return userInquiryRepository.findAll(pageable);
        }
        return userInquiryRepository.findByStatus(st, pageable);
    }

    /** (관리자) 상태 변경 — (id, status, adminId) */
    @Transactional
    public void updateStatus(Integer inquiryId, String status, Integer adminId) {
        UserInquiry inquiry = userInquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("문의가 존재하지 않습니다. id=" + inquiryId));

        Status newStatus;
        try {
            newStatus = Status.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("잘못된 상태 값: " + status);
        }

        inquiry.setStatus(newStatus);
        inquiry.setAnsweredBy(adminId);

        if (newStatus != Status.ANSWERED) {
            inquiry.setAnsweredAt(null);
        }

        userInquiryRepository.save(inquiry);
    }

    /** (관리자) 상태변경 DTO 버전 */
    @Transactional
    public void updateStatus(AdminStatusRequest req, Integer adminId) {
        updateStatus(req.getInquiryId(), req.getStatus(), adminId);
    }

    /** (관리자) 답변 등록 — (id, answer, adminId) */
    @Transactional
    public void answer(Integer inquiryId, String answer, Integer adminId) {
        answer(inquiryId, answer, adminId, null);
    }

    /** (관리자) 답변 등록 — (id, answer, adminId, assignedTo) */
    @Transactional
    public void answer(Integer inquiryId, String answer, Integer adminId, Integer assignedTo) {
        UserInquiry inq = userInquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("문의가 존재하지 않습니다. id=" + inquiryId));

        inq.setAdminAnswer(answer);
        inq.setStatus(Status.ANSWERED);
        inq.setAnsweredAt(LocalDateTime.now());
        inq.setAnsweredBy(adminId);
        if (Objects.nonNull(assignedTo)) {
            inq.setAssignedTo(assignedTo);
        }

        userInquiryRepository.save(inq);
    }
}
