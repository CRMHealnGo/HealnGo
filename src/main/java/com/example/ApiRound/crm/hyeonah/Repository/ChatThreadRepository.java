package com.example.ApiRound.crm.hyeonah.Repository;

import com.example.ApiRound.crm.hyeonah.entity.ChatThread;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatThreadRepository extends JpaRepository<ChatThread, Long> {
    Page<ChatThread> findByUserIdOrderByLastMsgAtDescThreadIdDesc(Integer userId, Pageable pageable);
    Page<ChatThread> findByCompanyIdOrderByLastMsgAtDescThreadIdDesc(Integer companyId, Pageable pageable);
    Optional<ChatThread> findFirstByUserIdAndCompanyIdAndItemId(Integer userId, Integer companyId, Long itemId);

}
