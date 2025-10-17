package com.example.ApiRound.crm.yoyo.reservation;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/company")
public class CompanyReservationController {

    @Autowired
    private ReservationService reservationService;

    // 업체 예약 관리 메인 페이지
    @GetMapping("/company_reservation_management")
    public String companyReservationManagement(
            @RequestParam(value = "companyId", required = false) Integer companyId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {

        // TODO: 실제 세션에서 companyId 가져오기
        if (companyId == null) {
            companyId = 1; // 임시값
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending().and(Sort.by("startTime").descending()));

        Page<ReservationDto> reservations;
        
        if (status != null && !status.isEmpty()) {
            reservations = reservationService.getCompanyReservationsByStatus(companyId, status)
                    .stream()
                    .skip(page * size)
                    .limit(size)
                    .collect(java.util.stream.Collectors.collectingAndThen(
                            java.util.stream.Collectors.toList(),
                            list -> new org.springframework.data.domain.PageImpl<>(list, pageable, list.size())
                    ));
        } else {
            reservations = reservationService.getCompanyReservations(companyId, pageable);
        }

        // 날짜별 필터링
        if (date != null && !date.isEmpty()) {
            LocalDate filterDate = LocalDate.parse(date);
            List<ReservationDto> dateFiltered = reservationService.getCompanyReservationsByDate(companyId, filterDate);
            reservations = new org.springframework.data.domain.PageImpl<>(dateFiltered, pageable, dateFiltered.size());
        }

        model.addAttribute("reservations", reservations);
        model.addAttribute("companyId", companyId);
        model.addAttribute("currentStatus", status);
        model.addAttribute("currentDate", date);
        
        // 통계 정보
        model.addAttribute("totalCount", reservationService.getReservationCountByCompanyAndStatus(companyId, null));
        model.addAttribute("confirmedCount", reservationService.getReservationCountByCompanyAndStatus(companyId, "CONFIRMED"));
        model.addAttribute("cancelledCount", reservationService.getReservationCountByCompanyAndStatus(companyId, "CANCELLED"));
        model.addAttribute("completedCount", reservationService.getReservationCountByCompanyAndStatus(companyId, "COMPLETED"));

        return "crm/company_reservation_management";
    }

    // 예약 상세 조회
    @GetMapping("/reservation/{id}")
    public String viewReservation(@PathVariable Long id, Model model) {
        try {
            ReservationDto reservation = reservationService.getReservationWithDetails(id);
            model.addAttribute("reservation", reservation);
            return "crm/reservation_detail";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "crm/company_reservation_management";
        }
    }

    // 예약 상태 변경
    @PostMapping("/reservation/{id}/status")
    public String updateReservationStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(value = "companyId") Integer companyId,
            RedirectAttributes redirectAttributes) {
        
        try {
            reservationService.updateReservationStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "예약 상태가 변경되었습니다.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/company/company_reservation_management?companyId=" + companyId;
    }

    // 예약 취소
    @PostMapping("/reservation/{id}/cancel")
    public String cancelReservation(
            @PathVariable Long id,
            @RequestParam(value = "companyId") Integer companyId,
            RedirectAttributes redirectAttributes) {
        
        try {
            reservationService.cancelReservation(id);
            redirectAttributes.addFlashAttribute("success", "예약이 취소되었습니다.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/company/company_reservation_management?companyId=" + companyId;
    }

    // 예약 완료 처리
    @PostMapping("/reservation/{id}/complete")
    public String completeReservation(
            @PathVariable Long id,
            @RequestParam(value = "companyId") Integer companyId,
            RedirectAttributes redirectAttributes) {
        
        try {
            reservationService.completeReservation(id);
            redirectAttributes.addFlashAttribute("success", "예약이 완료 처리되었습니다.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/company/company_reservation_management?companyId=" + companyId;
    }

    // 날짜별 예약 조회
    @GetMapping("/reservations/date")
    public String getReservationsByDate(
            @RequestParam Integer companyId,
            @RequestParam String date,
            Model model) {
        
        try {
            LocalDate filterDate = LocalDate.parse(date);
            List<ReservationDto> reservations = reservationService.getCompanyReservationsByDate(companyId, filterDate);
            model.addAttribute("reservations", reservations);
            model.addAttribute("companyId", companyId);
            model.addAttribute("selectedDate", date);
            return "crm/company_reservation_management";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "crm/company_reservation_management";
        }
    }
}
