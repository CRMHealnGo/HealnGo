package com.example.ApiRound.crm.hyeonah.Controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.ApiRound.crm.hyeonah.Service.ChatAppService;
import com.example.ApiRound.crm.hyeonah.dto.ChatMessageDto;
import com.example.ApiRound.crm.hyeonah.dto.ChatThreadDto;
import com.example.ApiRound.crm.hyeonah.dto.SenderRole;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatAppService chatAppService;

    //사용자 기준 스레드 목록
    @GetMapping("/threads/user/{userId}")
    public Page<ChatThreadDto> listThreadsByUser(@PathVariable Integer userId,
                                                 @RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "20") int size) {

        return  chatAppService.listThreadsForUser(userId,page,size);
    }

    // 회사 기준 스레드 목록
    @GetMapping("/threads")
    public Page<ChatThreadDto> listThreadsByCompany(@RequestParam Integer companyId,
                                                    @RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "20") int size,
                                                    @RequestParam(required = false) String status) {
        return chatAppService.listThreadsForCompany(companyId, page, size, status);
    }

    // 메시지 목록
    @GetMapping("/messages/{threadId}")
    public Page<ChatMessageDto> listMessages(@PathVariable Long threadId,
                                             @RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "20") int size) {
        return chatAppService.listMessages(threadId, page, size);
    }

    // 메시지 전송 (파일 포함)
    @PostMapping(value = "/message", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ChatMessageDto send(@RequestPart("threadId") Long threadId,
                               @RequestPart("senderRole") String senderRole,
                               @RequestPart(value = "senderUserId", required = false) Integer senderUserId,
                               @RequestPart(value = "senderCompanyId", required = false) Integer senderCompanyId,
                               @RequestPart(value = "body", required = false) String body,
                               @RequestPart(value = "file", required = false) MultipartFile file) throws Exception {

        byte[] bin = (file != null && !file.isEmpty()) ? file.getBytes() : null;
        String mime = (file != null) ? file.getContentType() : null;

        SenderRole role;
        try {
            role = SenderRole.valueOf(senderRole.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "senderRole must be SOCIAL or COMPANY");
        }

        return chatAppService.sendMessage(
                threadId,
                role,
                senderUserId,
                senderCompanyId,
                body,
                bin,
                mime
        );
    }

    // 메시지 전송 (JSON 형식 - 간단한 텍스트 전송용)
    @PostMapping("/send")
    public ChatMessageDto sendSimple(@RequestBody java.util.Map<String, Object> payload) {
        Long threadId = Long.valueOf(payload.get("threadId").toString());
        String senderRoleStr = (String) payload.get("senderRole");
        Integer senderUserId = payload.containsKey("senderUserId") ? 
            (Integer) payload.get("senderUserId") : null;
        Integer senderCompanyId = payload.containsKey("senderCompanyId") ? 
            (Integer) payload.get("senderCompanyId") : null;
        String body = (String) payload.get("body");

        SenderRole role;
        try {
            role = SenderRole.valueOf(senderRoleStr.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "senderRole must be SOCIAL or COMPANY");
        }

        return chatAppService.sendMessage(threadId, role, senderUserId, senderCompanyId, body, null, null);
    }

    // 스레드 상태 업데이트
    @PostMapping("/thread/{threadId}/status")
    public void updateThreadStatus(@PathVariable Long threadId, @RequestParam String status) {
        chatAppService.updateThreadStatus(threadId, status);
    }
}
