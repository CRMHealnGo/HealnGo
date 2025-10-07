package com.example.ApiRound.crm.hyeonah.Service;

import com.example.ApiRound.crm.hyeonah.entity.ReservationManagement;
import com.example.ApiRound.crm.hyeonah.Repository.ReservationManagementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReservationManagementServiceImpl implements ReservationManagementService {
    
    private final ReservationManagementRepository reservationManagementRepository;
    
    @Override
    public ReservationManagement createReservation(Map<String, String> eventData) {
        return createReservation(eventData, false);
    }
    
    @Override
    public ReservationManagement createReservation(Map<String, String> eventData, boolean googleSyncEnabled) {
        // 시간 충돌 검사
        LocalDate date = LocalDate.parse(eventData.get("date"));
        LocalTime startTime = LocalTime.parse(eventData.get("startTime"));
        LocalTime endTime = LocalTime.parse(eventData.get("endTime"));
        
        if (!isTimeSlotAvailable(date, startTime, endTime, null)) {
            throw new IllegalArgumentException("선택한 시간대에 이미 예약이 있습니다.");
        }
        
        // 예약 생성
        ReservationManagement reservation = ReservationManagement.builder()
                .title(eventData.get("title"))
                .date(date)
                .startTime(startTime)
                .endTime(endTime)
                .description(eventData.get("description"))
                .location(eventData.get("location"))
                .status(ReservationManagement.ReservationManagementStatus.CONFIRMED)
                .googleSyncEnabled(googleSyncEnabled)
                .build();
        
        ReservationManagement savedReservation = reservationManagementRepository.save(reservation);
        log.info("새 예약 생성: ID={}, 제목={}, 날짜={}", savedReservation.getId(), savedReservation.getTitle(), savedReservation.getDate());
        
        return savedReservation;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ReservationManagement> findById(Long id) {
        return reservationManagementRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReservationManagement> findAll() {
        return reservationManagementRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReservationManagement> findByDate(LocalDate date) {
        return reservationManagementRepository.findByDateOrderByStartTimeAsc(date);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReservationManagement> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return reservationManagementRepository.findByDateBetweenOrderByDateAscStartTimeAsc(startDate, endDate);
    }
    
    @Override
    public ReservationManagement updateReservation(Long id, Map<String, String> eventData) {
        ReservationManagement reservation = reservationManagementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다: " + id));
        
        // 시간 충돌 검사 (자신 제외)
        LocalDate date = LocalDate.parse(eventData.get("date"));
        LocalTime startTime = LocalTime.parse(eventData.get("startTime"));
        LocalTime endTime = LocalTime.parse(eventData.get("endTime"));
        
        if (!isTimeSlotAvailable(date, startTime, endTime, id)) {
            throw new IllegalArgumentException("선택한 시간대에 이미 예약이 있습니다.");
        }
        
        // 예약 정보 업데이트
        reservation.setTitle(eventData.get("title"));
        reservation.setDate(date);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setDescription(eventData.get("description"));
        reservation.setLocation(eventData.get("location"));
        
        ReservationManagement updatedReservation = reservationManagementRepository.save(reservation);
        log.info("예약 수정: ID={}, 제목={}", updatedReservation.getId(), updatedReservation.getTitle());
        
        return updatedReservation;
    }
    
    @Override
    public void deleteReservation(Long id) {
        ReservationManagement reservation = reservationManagementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다: " + id));
        
        reservationManagementRepository.delete(reservation);
        log.info("예약 삭제: ID={}, 제목={}", id, reservation.getTitle());
    }
    
    @Override
    public ReservationManagement updateReservationStatus(Long id, ReservationManagement.ReservationManagementStatus status) {
        ReservationManagement reservation = reservationManagementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다: " + id));
        
        reservation.setStatus(status);
        ReservationManagement updatedReservation = reservationManagementRepository.save(reservation);
        log.info("예약 상태 변경: ID={}, 상태={}", id, status);
        
        return updatedReservation;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isTimeSlotAvailable(LocalDate date, LocalTime startTime, LocalTime endTime, Long excludeReservationId) {
        List<ReservationManagement> overlappingReservations = reservationManagementRepository.findOverlappingReservations(date, startTime, endTime);
        
        // 자신을 제외하고 충돌하는 예약이 있는지 확인
        return overlappingReservations.stream()
                .noneMatch(reservation -> !reservation.getId().equals(excludeReservationId));
    }
    
    @Override
    public ReservationManagement syncToGoogleCalendar(Long reservationId) {
        // TODO: 구글 캘린더 API 연동 로직 구현
        ReservationManagement reservation = reservationManagementRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다: " + reservationId));
        
        // 구글 캘린더 동기화 활성화
        reservation.setGoogleSyncEnabled(true);
        // TODO: 실제 구글 이벤트 생성 후 googleEventId 설정
        
        ReservationManagement updatedReservation = reservationManagementRepository.save(reservation);
        log.info("구글 캘린더 동기화 활성화: ID={}", reservationId);
        
        return updatedReservation;
    }
    
    @Override
    public ReservationManagement unsyncFromGoogleCalendar(Long reservationId) {
        ReservationManagement reservation = reservationManagementRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다: " + reservationId));
        
        // 구글 캘린더 동기화 해제
        reservation.setGoogleSyncEnabled(false);
        reservation.setGoogleEventId(null);
        
        ReservationManagement updatedReservation = reservationManagementRepository.save(reservation);
        log.info("구글 캘린더 동기화 해제: ID={}", reservationId);
        
        return updatedReservation;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Integer> getReservationCountsByDate(LocalDate startDate, LocalDate endDate) {
        List<ReservationManagement> reservations = reservationManagementRepository.findByDateBetweenOrderByDateAscStartTimeAsc(startDate, endDate);
        Map<String, Integer> counts = new HashMap<>();
        
        for (ReservationManagement reservation : reservations) {
            String dateKey = reservation.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
            counts.put(dateKey, counts.getOrDefault(dateKey, 0) + 1);
        }
        
        return counts;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReservationManagement> getRecentReservations(int limit) {
        LocalDate today = LocalDate.now();
        return reservationManagementRepository.findUpcomingReservations(today).stream()
                .limit(limit)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReservationManagement> getTodayReservations() {
        return reservationManagementRepository.findByDateOrderByStartTimeAsc(LocalDate.now());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReservationManagement> searchReservations(String keyword) {
        return reservationManagementRepository.findByTitleContainingIgnoreCaseOrderByDateAscStartTimeAsc(keyword);
    }
}