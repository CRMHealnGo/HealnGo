package com.example.ApiRound.crm.hyeonah.Controller;

import com.example.ApiRound.crm.hyeonah.Service.ChatAppService;
import com.example.ApiRound.crm.hyeonah.dto.ChatMessageDto;
import com.example.ApiRound.crm.hyeonah.dto.ChatThreadDto;
import com.example.ApiRound.crm.hyeonah.dto.SenderRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

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
}
