package com.example.ApiRound.repository;

import com.example.ApiRound.entity.ClickLog;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClickLogRepository extends JpaRepository<ClickLog, Long> {

    // 최근 기간 내 회사별 클릭 상위
    @Query("""
           SELECT c.companyId AS companyId, COUNT(c) AS clickCount
             FROM ClickLog c
            WHERE c.clickedAt >= :startDate
            GROUP BY c.companyId
            ORDER BY clickCount DESC
           """)
    List<Object[]> findTopCompaniesByClickCount(@Param("startDate") LocalDateTime startDate);

    long countByCompanyIdAndClickedAtAfter(Long companyId, LocalDateTime clickedAt);
    long countByCompanyIdAndClickedAtBetween(Long companyId, LocalDateTime start, LocalDateTime end);
}
