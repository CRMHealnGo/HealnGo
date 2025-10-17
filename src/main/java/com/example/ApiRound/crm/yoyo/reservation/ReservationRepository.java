package com.example.ApiRound.crm.yoyo.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ApiRound.crm.hyeonah.entity.SocialUsers;
import com.example.ApiRound.crm.hyeonah.entity.CompanyUser;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // 특정 사용자의 예약 목록 조회
    List<Reservation> findByUserOrderByDateDescStartTimeDesc(SocialUsers user);

    // 특정 업체의 예약 목록 조회
    List<Reservation> findByCompanyOrderByDateDescStartTimeDesc(CompanyUser company);

    // 특정 업체의 예약 목록 조회 (페이징)
    Page<Reservation> findByCompanyOrderByDateDescStartTimeDesc(CompanyUser company, Pageable pageable);

    // 특정 날짜의 예약 목록 조회
    List<Reservation> findByCompanyAndDateOrderByStartTimeAsc(CompanyUser company, LocalDate date);

    // 특정 날짜와 시간대의 예약 확인 (중복 예약 방지)
    @Query("SELECT r FROM Reservation r WHERE r.company = :company AND r.date = :date AND r.status = 'CONFIRMED' AND " +
           "((r.startTime <= :startTime AND r.endTime > :startTime) OR " +
           "(r.startTime < :endTime AND r.endTime >= :endTime) OR " +
           "(r.startTime >= :startTime AND r.endTime <= :endTime))")
    List<Reservation> findOverlappingReservations(@Param("company") CompanyUser company, 
                                                  @Param("date") LocalDate date,
                                                  @Param("startTime") LocalTime startTime,
                                                  @Param("endTime") LocalTime endTime);

    // 예약 상태별 조회
    List<Reservation> findByCompanyAndStatusOrderByDateDescStartTimeDesc(CompanyUser company, String status);

    // 특정 사용자의 예약 상태별 조회
    List<Reservation> findByUserAndStatusOrderByDateDescStartTimeDesc(SocialUsers user, String status);

    // 예약 상세 조회 (FK 정보 포함)
    @Query("SELECT r FROM Reservation r " +
           "LEFT JOIN FETCH r.user " +
           "LEFT JOIN FETCH r.company " +
           "WHERE r.id = :id")
    Optional<Reservation> findByIdWithDetails(@Param("id") Long id);

    // 업체별 예약 통계
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.company = :company AND r.status = :status")
    Long countByCompanyAndStatus(@Param("company") CompanyUser company, @Param("status") String status);

    // 날짜 범위별 예약 조회
    @Query("SELECT r FROM Reservation r WHERE r.company = :company AND r.date BETWEEN :startDate AND :endDate ORDER BY r.date ASC, r.startTime ASC")
    List<Reservation> findByCompanyAndDateBetween(@Param("company") CompanyUser company, 
                                                  @Param("startDate") LocalDate startDate, 
                                                  @Param("endDate") LocalDate endDate);

    // 전체 예약 목록 조회 (관리자용)
    @Query("SELECT r FROM Reservation r " +
           "LEFT JOIN FETCH r.user " +
           "LEFT JOIN FETCH r.company " +
           "ORDER BY r.date DESC, r.startTime DESC")
    Page<Reservation> findAllWithDetails(Pageable pageable);

    // 관리자용 - 특정 상태별 예약 조회
    @Query("SELECT r FROM Reservation r " +
           "LEFT JOIN FETCH r.user " +
           "LEFT JOIN FETCH r.company " +
           "WHERE r.status = :status " +
           "ORDER BY r.date DESC, r.startTime DESC")
    Page<Reservation> findByStatusWithDetails(@Param("status") String status, Pageable pageable);
    
    // 업체별 전체 예약 수 조회
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.company = :company")
    Long countByCompany(@Param("company") CompanyUser company);
    
    // 생성일(created_at) 기준 날짜 범위별 예약 조회
    @Query("SELECT r FROM Reservation r WHERE r.company = :company AND r.createdAt BETWEEN :startDateTime AND :endDateTime ORDER BY r.createdAt ASC")
    List<Reservation> findByCompanyAndCreatedAtBetween(@Param("company") CompanyUser company, 
                                                        @Param("startDateTime") LocalDateTime startDateTime, 
                                                        @Param("endDateTime") LocalDateTime endDateTime);
    
    // 사용자 ID로 예약 조회 (페이징)
    @Query("SELECT r FROM Reservation r " +
           "LEFT JOIN FETCH r.company " +
           "WHERE r.user.userId = :userId " +
           "ORDER BY r.createdAt DESC")
    Page<Reservation> findByUser_UserId(@Param("userId") Integer userId, Pageable pageable);
}
