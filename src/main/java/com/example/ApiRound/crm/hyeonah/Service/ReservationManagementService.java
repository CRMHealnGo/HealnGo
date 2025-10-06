package com.example.ApiRound.crm.hyeonah.Service;

import com.example.ApiRound.crm.hyeonah.entity.ReservationManagement;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ReservationManagementService {
    
    // 예약 생성
    ReservationManagement createReservation(Map<String, String> eventData);
    
    // 예약 생성 (구글 동기화 옵션 포함)
    ReservationManagement createReservation(Map<String, String> eventData, boolean googleSyncEnabled);
    
    // 예약 조회
    Optional<ReservationManagement> findById(Long id);
    
    // 모든 예약 조회
    List<ReservationManagement> findAll();
    
    // 날짜별 예약 조회
    List<ReservationManagement> findByDate(LocalDate date);
    
    // 날짜 범위별 예약 조회
    List<ReservationManagement> findByDateRange(LocalDate startDate, LocalDate endDate);
    
    // 예약 수정
    ReservationManagement updateReservation(Long id, Map<String, String> eventData);
    
    // 예약 삭제
    void deleteReservation(Long id);
    
    // 예약 상태 변경
    ReservationManagement updateReservationStatus(Long id, ReservationManagement.ReservationManagementStatus status);
    
    // 시간 충돌 검사
    boolean isTimeSlotAvailable(LocalDate date, LocalTime startTime, LocalTime endTime, Long excludeReservationId);
    
    // 구글 캘린더 동기화
    ReservationManagement syncToGoogleCalendar(Long reservationId);
    
    // 구글 캘린더에서 동기화 해제
    ReservationManagement unsyncFromGoogleCalendar(Long reservationId);
    
    // 달력용 예약 데이터 조회 (날짜별 개수)
    Map<String, Integer> getReservationCountsByDate(LocalDate startDate, LocalDate endDate);
    
    // 최근 예약 조회
    List<ReservationManagement> getRecentReservations(int limit);
    
    // 오늘 예약 조회
    List<ReservationManagement> getTodayReservations();
    
    // 예약 검색
    List<ReservationManagement> searchReservations(String keyword);
}