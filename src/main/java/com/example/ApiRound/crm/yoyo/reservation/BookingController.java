package com.example.ApiRound.crm.yoyo.reservation;

import com.example.ApiRound.crm.hyeonah.entity.SocialUsers;
import com.example.ApiRound.crm.hyeonah.Repository.SocialUsersRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class BookingController {

    private final ReservationRepository reservationRepository;
    private final SocialUsersRepository socialUsersRepository;

    public BookingController(ReservationRepository reservationRepository, SocialUsersRepository socialUsersRepository) {
        this.reservationRepository = reservationRepository;
        this.socialUsersRepository = socialUsersRepository;
    }

    @GetMapping("/booking")
    public String booking(HttpSession session, Model model) {
        // 세션에서 사용자 ID 가져오기
        Integer userId = (Integer) session.getAttribute("userId");
        
        if (userId == null) {
            // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
            return "redirect:/login";
        }
        
        // 사용자 정보 조회
        SocialUsers user = socialUsersRepository.findById(userId).orElse(null);
        
        if (user == null) {
            return "redirect:/login";
        }
        
        // 해당 사용자의 예약 내역 조회
        List<Reservation> reservations = reservationRepository.findByUserOrderByDateDescStartTimeDesc(user);
        
        model.addAttribute("reservations", reservations);
        
        return "crm/booking";
    }
    
    /**
     * 예약 상세 정보 조회 API
     */
    @GetMapping("/api/reservations/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getReservationDetail(@PathVariable Long id, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of(
                "success", false,
                "message", "로그인이 필요합니다."
            ));
        }
        
        Reservation reservation = reservationRepository.findByIdWithDetails(id).orElse(null);
        
        if (reservation == null) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "예약 정보를 찾을 수 없습니다."
            ));
        }
        
        // 본인의 예약인지 확인
        if (reservation.getUser() == null || !reservation.getUser().getUserId().equals(userId)) {
            return ResponseEntity.status(403).body(Map.of(
                "success", false,
                "message", "접근 권한이 없습니다."
            ));
        }
        
        Map<String, Object> reservationData = new HashMap<>();
        reservationData.put("id", reservation.getId());
        reservationData.put("title", reservation.getTitle());
        reservationData.put("companyName", reservation.getCompany() != null ? reservation.getCompany().getCompanyName() : null);
        reservationData.put("date", reservation.getDate() != null ? reservation.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null);
        reservationData.put("startTime", reservation.getStartTime() != null ? reservation.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) : null);
        reservationData.put("endTime", reservation.getEndTime() != null ? reservation.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")) : null);
        reservationData.put("location", reservation.getLocation());
        reservationData.put("status", reservation.getStatus());
        reservationData.put("totalAmount", reservation.getTotalAmount());
        reservationData.put("description", reservation.getDescription());
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "reservation", reservationData
        ));
    }
    
    /**
     * 예약 취소 API
     */
    @PostMapping("/api/reservations/{id}/cancel")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cancelReservation(@PathVariable Long id, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of(
                "success", false,
                "message", "로그인이 필요합니다."
            ));
        }
        
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        
        if (reservation == null) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "예약 정보를 찾을 수 없습니다."
            ));
        }
        
        // 본인의 예약인지 확인
        if (reservation.getUser() == null || !reservation.getUser().getUserId().equals(userId)) {
            return ResponseEntity.status(403).body(Map.of(
                "success", false,
                "message", "접근 권한이 없습니다."
            ));
        }
        
        // 이미 취소된 예약인지 확인
        if ("CANCELLED".equals(reservation.getStatus())) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "이미 취소된 예약입니다."
            ));
        }
        
        // 예약 상태를 CANCELLED로 변경
        reservation.setStatus("CANCELLED");
        reservationRepository.save(reservation);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "예약이 취소되었습니다."
        ));
    }
}


