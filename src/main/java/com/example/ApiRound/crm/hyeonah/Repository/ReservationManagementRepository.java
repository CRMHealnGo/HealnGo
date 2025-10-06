package com.example.ApiRound.crm.hyeonah.Repository;

import com.example.ApiRound.crm.hyeonah.entity.ReservationManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationManagementRepository extends JpaRepository<ReservationManagement, Long> {
    
    // 날짜별 예약 조회
    List<ReservationManagement> findByDateOrderByStartTimeAsc(LocalDate date);
    
    // 날짜 범위별 예약 조회
    List<ReservationManagement> findByDateBetweenOrderByDateAscStartTimeAsc(LocalDate startDate, LocalDate endDate);
    
    // 상태별 예약 조회
    List<ReservationManagement> findByStatusOrderByDateAscStartTimeAsc(ReservationManagement.ReservationManagementStatus status);
    
    // 구글 동기화가 활성화된 예약 조회
    List<ReservationManagement> findByGoogleSyncEnabledTrue();
    
    // 구글 이벤트 ID로 예약 조회
    Optional<ReservationManagement> findByGoogleEventId(String googleEventId);
    
    // 특정 날짜와 시간대에 겹치는 예약 조회
    @Query("SELECT r FROM ReservationManagement r WHERE r.date = :date " +
           "AND r.status != 'CANCELLED' " +
           "AND ((r.startTime <= :startTime AND r.endTime > :startTime) OR " +
           "     (r.startTime < :endTime AND r.endTime >= :endTime) OR " +
           "     (r.startTime >= :startTime AND r.endTime <= :endTime))")
    List<ReservationManagement> findOverlappingReservations(
        @Param("date") LocalDate date,
        @Param("startTime") java.time.LocalTime startTime,
        @Param("endTime") java.time.LocalTime endTime
    );
    
    // 제목으로 예약 검색
    List<ReservationManagement> findByTitleContainingIgnoreCaseOrderByDateAscStartTimeAsc(String title);
    
    // 최근 예약 조회 (오늘부터)
    @Query("SELECT r FROM ReservationManagement r WHERE r.date >= :today ORDER BY r.date ASC, r.startTime ASC")
    List<ReservationManagement> findUpcomingReservations(@Param("today") LocalDate today);
    
    // 통계용 쿼리들
    @Query("SELECT COUNT(r) FROM ReservationManagement r WHERE r.date = :date AND r.status != 'CANCELLED'")
    Long countByDate(@Param("date") LocalDate date);
    
    @Query("SELECT COUNT(r) FROM ReservationManagement r WHERE r.status = :status")
    Long countByStatus(@Param("status") ReservationManagement.ReservationManagementStatus status);
}