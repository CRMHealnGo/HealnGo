package com.example.ApiRound.crm.hyeonah.Service;

import com.example.ApiRound.crm.hyeonah.Repository.ChatMessageRepository;
import com.example.ApiRound.crm.hyeonah.Repository.ChatThreadRepository;
import com.example.ApiRound.crm.hyeonah.dto.ChatMessageDto;
import com.example.ApiRound.crm.hyeonah.dto.ChatThreadDto;
import com.example.ApiRound.crm.hyeonah.dto.SenderRole;
import com.example.ApiRound.crm.hyeonah.entity.ChatMessage;
import com.example.ApiRound.crm.hyeonah.entity.ChatThread;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatAppService {

    private final ChatThreadRepository threadRepo;
    private final ChatMessageRepository msgRepo;

    // 스레드 보장 (없으면 생성)
    @Transactional
    public Long ensureThread(Integer userId, Integer companyId, Long itemId, String title) {
        return threadRepo.findFirstByUserIdAndCompanyIdAndItemId(userId, companyId, itemId)
                .map(ChatThread::getThreadId)
                .orElseGet(() -> threadRepo.save(ChatThread.builder()
                        .userId(userId)
                        .companyId(companyId)
                        .itemId(itemId)
                        .title(title)
                        .muted(false)
                        .lastMsgAt(LocalDateTime.now())
                        .build()
                ).getThreadId());
    }

    // 사용자용 스레드 목록
    @Transactional(readOnly = true)
    public Page<ChatThreadDto> listThreadsForUser(Integer userId, int page, int size) {
        var p = threadRepo.findByUserIdOrderByLastMsgAtDescThreadIdDesc(userId, PageRequest.of(page - 1, size));
        return p.map(t -> ChatThreadDto.builder()
                .threadId(t.getThreadId())
                .userId(t.getUserId())
                .companyId(t.getCompanyId())
                .itemId(t.getItemId())
                .title(t.getTitle())
                .muted(t.isMuted())
                .lastMsgAt(t.getLastMsgAt())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build());
    }

    // 메시지 목록
    @Transactional(readOnly = true)
    public Page<ChatMessageDto> listMessages(Long threadId, int page, int size) {
        var thread = threadRepo.findById(threadId).orElseThrow();
        var p = msgRepo.findByThreadOrderByCreatedAtDesc(thread, PageRequest.of(page - 1, size));
        return p.map(m -> ChatMessageDto.builder()
                .messageId(m.getMessageId())
                .threadId(thread.getThreadId())                 // ✅ Long
                .senderRole(m.getSenderRole())                  // ✅ enum SenderRole
                .senderUserId(m.getSenderUserId())
                .senderCompanyId(m.getSenderCompanyId())
                .body(m.getBody())
                // .attachmentBlob(null)  // 목록에선 보통 제외 (원하면 넣어도 됨)
                .attachmentMime(m.getAttachmentMime())
                .createdAt(m.getCreatedAt())
                .build());
    }

    // 메시지 전송
    @Transactional
    public ChatMessageDto sendMessage(Long  threadId, SenderRole role, Integer senderUserId,
                                      Integer senderCompanyId, String body, byte[] file, String mime) {

        var thread = threadRepo.getReferenceById(threadId);

        var saved = msgRepo.save(ChatMessage.builder()
                .thread(thread)
                .senderRole(role)
                .senderUserId(senderUserId)
                .senderCompanyId(senderCompanyId)
                .body(body)
                .attachmentBlob(file)
                .attachmentMime(mime)
                .build());

        // 마지막 메시지 시각 갱신
        thread.setLastMsgAt(LocalDateTime.now());

        return ChatMessageDto.builder()
                .messageId(saved.getMessageId())
                .threadId(threadId)
                .senderRole(saved.getSenderRole())
                .senderUserId(saved.getSenderUserId())
                .senderCompanyId(saved.getSenderCompanyId())
                .body(saved.getBody())
                // .attachmentBlob(saved.getAttachmentBlob()) // 필요 시 포함
                .attachmentMime(saved.getAttachmentMime())
                .createdAt(saved.getCreatedAt())
                .build();
    }
}
