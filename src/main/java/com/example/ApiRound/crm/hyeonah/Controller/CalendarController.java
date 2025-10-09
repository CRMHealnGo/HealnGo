package com.example.ApiRound.crm.hyeonah.Controller;

import com.example.ApiRound.crm.hyeonah.entity.ReservationManagement;
import com.example.ApiRound.crm.hyeonah.Service.ReservationManagementService;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CalendarController {

    private final ReservationManagementService reservationManagementService;
    
    @Value("${google.client-id:}")
    private String googleClientId;

    @Value("${google.calendar.application.name:HealnGo Calendar}")
    private String applicationName;
    
    public CalendarController(ReservationManagementService reservationManagementService) {
        this.reservationManagementService = reservationManagementService;
    }

    @GetMapping("/admin/reservation-management")
    public String reservationManagement(Model model) {
        model.addAttribute("sidebarType", "admin");
        return "admin/reservations";
    }

    @GetMapping("/company/company_reservation_management")
    public String companyReservationManagement(Model model) {
        model.addAttribute("sidebarType", "company");
        return "crm/company_reservation_management";
    }

    @GetMapping("/api/google/events")
    @ResponseBody
    public List<Map<String, Object>> getReservations() {
        try {
            // DB에서 예약 데이터 조회 (오늘부터 30일 후까지)
            LocalDate today = LocalDate.now();
            LocalDate thirtyDaysLater = today.plusDays(30);
            
            List<ReservationManagement> reservations = reservationManagementService.findByDateRange(today, thirtyDaysLater);
            
            List<Map<String, Object>> eventList = new ArrayList<>();
            
            for (ReservationManagement reservation : reservations) {
                Map<String, Object> eventMap = new HashMap<>();
                eventMap.put("id", reservation.getId());
                eventMap.put("summary", reservation.getTitle());
                eventMap.put("status", reservation.getStatus().name().toLowerCase());
                eventMap.put("description", reservation.getDescription());
                eventMap.put("location", reservation.getLocation());
                eventMap.put("googleSyncEnabled", reservation.getGoogleSyncEnabled());
                eventMap.put("googleEventId", reservation.getGoogleEventId());
                
                // 날짜와 시간을 ISO 형식으로 변환
                String startDateTime = reservation.getDate().atTime(reservation.getStartTime())
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "+09:00";
                String endDateTime = reservation.getDate().atTime(reservation.getEndTime())
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "+09:00";
                
                eventMap.put("start", startDateTime);
                eventMap.put("end", endDateTime);
                
                eventList.add(eventMap);
            }
            
            return eventList;
            
        } catch (Exception e) {
            // 오류 발생 시 더미 데이터 반환
            System.err.println("예약 조회 오류: " + e.getMessage());
            return getDummyEvents();
        }
    }
    
    private Calendar createCalendarService() throws Exception {
        // 인증이 필요한 경우 여기에 OAuth2 토큰 설정
        // 현재는 인증 없이 공개 캘린더만 접근 가능
        
        HttpRequestInitializer requestInitializer = request -> {
            // 인증 헤더 설정 (필요시)
            // request.getHeaders().setAuthorization("Bearer " + accessToken);
        };
        
        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer
        ).setApplicationName(applicationName).build();
    }
    
    private List<Map<String, Object>> getDummyEvents() {
        List<Map<String, Object>> dummyEvents = new ArrayList<>();
        
        Map<String, Object> event1 = new HashMap<>();
        event1.put("id", "dummy1");
        event1.put("summary", "예약 1");
        event1.put("status", "confirmed");
        event1.put("start", "2024-10-05T09:00:00+09:00");
        event1.put("end", "2024-10-05T10:00:00+09:00");
        dummyEvents.add(event1);
        
        Map<String, Object> event2 = new HashMap<>();
        event2.put("id", "dummy2");
        event2.put("summary", "예약 2");
        event2.put("status", "confirmed");
        event2.put("start", "2024-10-05T14:00:00+09:00");
        event2.put("end", "2024-10-05T15:00:00+09:00");
        dummyEvents.add(event2);
        
        return dummyEvents;
    }

    // 일정 추가 API
    @PostMapping("/api/google/events")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createEvent(@RequestBody Map<String, String> eventData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 구글 동기화 옵션 확인 (기본값: false)
            boolean googleSyncEnabled = "true".equals(eventData.get("googleSyncEnabled"));
            
            // DB에 예약 저장
            ReservationManagement reservation = reservationManagementService.createReservation(eventData, googleSyncEnabled);
            
            // 구글 동기화가 활성화된 경우 구글 캘린더에도 저장
            if (googleSyncEnabled) {
                try {
                    syncToGoogleCalendar(reservation, eventData);
                } catch (Exception e) {
                    System.err.println("구글 캘린더 동기화 실패: " + e.getMessage());
                    // 구글 동기화 실패해도 DB 저장은 유지
                }
            }
            
            response.put("success", true);
            response.put("message", "일정이 성공적으로 추가되었습니다.");
            response.put("eventId", reservation.getId());
            response.put("googleSyncEnabled", reservation.getGoogleSyncEnabled());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(400).body(response);
            
        } catch (Exception e) {
            System.err.println("일정 추가 오류: " + e.getMessage());
            
            response.put("success", false);
            response.put("message", "일정 추가 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    // 일정 삭제 API
    @DeleteMapping("/api/google/events/{eventId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteEvent(@PathVariable String eventId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // DB에서 예약 삭제
            reservationManagementService.deleteReservation(Long.parseLong(eventId));
            
            response.put("success", true);
            response.put("message", "일정이 성공적으로 삭제되었습니다.");
            
            return ResponseEntity.ok(response);
            
        } catch (NumberFormatException e) {
            response.put("success", false);
            response.put("message", "잘못된 예약 ID입니다.");
            return ResponseEntity.status(400).body(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(404).body(response);
            
        } catch (Exception e) {
            System.err.println("일정 삭제 오류: " + e.getMessage());
            
            response.put("success", false);
            response.put("message", "일정 삭제 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // 구글 캘린더 동기화 헬퍼 메서드
    private void syncToGoogleCalendar(ReservationManagement reservation, Map<String, String> eventData) throws Exception {
        Calendar calendar = createCalendarService();
        
        // 새 이벤트 생성
        Event event = new Event()
            .setSummary(eventData.get("title"))
            .setDescription(eventData.get("description"))
            .setLocation(eventData.get("location"));

        // 시작 시간 설정
        String dateStr = eventData.get("date");
        String startTimeStr = eventData.get("startTime");
        String endTimeStr = eventData.get("endTime");
        
        LocalDateTime startDateTime = LocalDateTime.parse(dateStr + "T" + startTimeStr);
        LocalDateTime endDateTime = LocalDateTime.parse(dateStr + "T" + endTimeStr);
        
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String startDateTimeStr = startDateTime.format(formatter);
        String endDateTimeStr = endDateTime.format(formatter);
        
        EventDateTime start = new EventDateTime()
            .setDateTime(new com.google.api.client.util.DateTime(startDateTimeStr + "+09:00"))
            .setTimeZone("Asia/Seoul");
        event.setStart(start);

        EventDateTime end = new EventDateTime()
            .setDateTime(new com.google.api.client.util.DateTime(endDateTimeStr + "+09:00"))
            .setTimeZone("Asia/Seoul");
        event.setEnd(end);

        // 구글 캘린더에 이벤트 저장
        Event createdEvent = calendar.events().insert("primary", event).execute();
        
        // DB의 예약에 구글 이벤트 ID 저장
        reservation.setGoogleEventId(createdEvent.getId());
        // TODO: reservationManagementService.updateReservation 호출하여 DB 업데이트
    }
}
