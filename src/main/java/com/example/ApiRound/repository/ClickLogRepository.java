package com.example.ApiRound.repository;

import com.example.ApiRound.entity.ClickLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClickLogRepository extends JpaRepository<ClickLog, Long> {
    
    // 최근 7일간 상위 3개 회사 조회
    @Query("SELECT c.companyId as companyId, COUNT(c) as clickCount " +
           "FROM ClickLog c " +
           "WHERE c.clickedAt >= :startDate " +
           "GROUP BY c.companyId " +
           "ORDER BY clickCount DESC")
    List<Object[]> findTopCompaniesByClickCount(@Param("startDate") LocalDateTime startDate);
    
    // 특정 회사의 클릭 로그 저장
    ClickLog save(ClickLog clickLog);
}
