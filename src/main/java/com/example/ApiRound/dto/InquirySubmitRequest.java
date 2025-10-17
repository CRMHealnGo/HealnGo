package com.example.ApiRound.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class InquirySubmitRequest {
    private Integer orderId;
    private String subject;
    private String content;
    private MultipartFile attachment;  // 파일 저장 로직 필요시 서비스에서 처리
}
