package com.example.ApiRound.crm.minggzz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.ApiRound.crm.hyeonah.Service.CompanyUserService;
import com.example.ApiRound.crm.hyeonah.entity.CompanyUser;
import com.example.ApiRound.crm.yoyo.reservation.Reservation;
import com.example.ApiRound.crm.yoyo.reservation.ReservationRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/company")
public class CompanyController {
    
    private final CompanyUserService companyUserService;
    private final ReservationRepository reservationRepo;
    
    @Autowired
    public CompanyController(CompanyUserService companyUserService, ReservationRepository reservationRepo) {
        this.companyUserService = companyUserService;
        this.reservationRepo = reservationRepo;
    }

    /**
     * 테스트 엔드포인트
     */
    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "Company Controller is working!";
    }

    /**
     * 업체 메인페이지 (힝거 피부과)
     */
    @GetMapping("/dashboard")
    public String company(HttpSession session, Model model) {
        // 세션 체크: 업체로 로그인한 사용자만 접근 가능
        Integer companyId = (Integer) session.getAttribute("companyId");
        String userType = (String) session.getAttribute("userType");

        if (companyId == null || !"company".equals(userType)) {
            return "redirect:/crm/crm_login";
        }

        // 업체 정보 추가
        model.addAttribute("companyId", companyId);
        model.addAttribute("companyName", session.getAttribute("companyName"));
        model.addAttribute("companyEmail", session.getAttribute("companyEmail"));
        // 업체 대시보드 데이터 (실제로는 서비스에서 가져와야 함)
        Map<String, Object> companyStats = new HashMap<>();
        companyStats.put("foreignTouristIncrease", "15% 증가");
        companyStats.put("koreanTouristIncrease", "5% 증가");
        companyStats.put("newEventProducts", "4건");

        model.addAttribute("companyStats", companyStats);

        // 예약 차트 데이터 - 실제 DB 데이터 사용
        Optional<CompanyUser> companyOpt = companyUserService.findById(companyId);
        if (companyOpt.isPresent()) {
            Map<String, Object> reservationChartData = getReservationChartData(companyOpt.get());
            model.addAttribute("reservationChartData", reservationChartData);
        } else {
            Map<String, Object> reservationChartData = getReservationChartData(null);
            model.addAttribute("reservationChartData", reservationChartData);
        }

        // 후기 데이터
        List<Map<String, Object>> reviews = getCompanyReviews();
        model.addAttribute("reviews", reviews);

        // 인기 이벤트 데이터
        List<Map<String, Object>> popularEvents = getPopularEvents();
        model.addAttribute("popularEvents", popularEvents);

        model.addAttribute("sidebarType", "company");
        addAvatarInfo(model, companyId);
        return "crm/company";
    }

    /**
     * 이벤트 등록 페이지
     */
    @GetMapping("/event-registration")
    public String eventRegistration(HttpSession session, Model model) {
        Integer companyId = (Integer) session.getAttribute("companyId");
        model.addAttribute("sidebarType", "company");
        model.addAttribute("companyName", session.getAttribute("companyName"));
        model.addAttribute("companyId", companyId);
        addAvatarInfo(model, companyId);
        return "crm/event_registration";
    }

    /**
     * 문의 & 채팅 페이지
     */
    @GetMapping("/inquiry-chat")
    public String inquiryChat(HttpSession session, Model model) {
        Integer companyId = (Integer) session.getAttribute("companyId");
        List<Map<String, Object>> inquiries = getInquiries();
        model.addAttribute("inquiries", inquiries);
        model.addAttribute("companyName", session.getAttribute("companyName"));
        model.addAttribute("companyId", companyId);
        model.addAttribute("sidebarType", "company");
        addAvatarInfo(model, companyId);
        return "crm/company_inquiry_chat";
    }

    /**
     * 문의/신고 접수 페이지
     */
    @GetMapping("/inquiry-report")
    public String inquiryReport(HttpSession session, Model model) {
        Integer companyId = (Integer) session.getAttribute("companyId");
        List<Map<String, Object>> reports = getInquiryReports();
        model.addAttribute("reports", reports);
        model.addAttribute("sidebarType", "company");
        model.addAttribute("companyName", session.getAttribute("companyName"));
        model.addAttribute("companyId", companyId);
        addAvatarInfo(model, companyId);
        return "crm/inquiry_report";
    }

    /**
     * 문의/신고 상세 페이지 (업체용)
     */
    @GetMapping("/inquiry-report/detail/{id}")
    public String inquiryReportDetail(@PathVariable("id") Long id, Model model, HttpSession session) {
        Integer companyId = (Integer) session.getAttribute("companyId");
        Map<String, Object> report = getInquiryReportById(id);
        model.addAttribute("report", report);
        model.addAttribute("sidebarType", "company");
        model.addAttribute("companyName", session.getAttribute("companyName"));
        model.addAttribute("companyId", companyId);
        addAvatarInfo(model, companyId);
        return "crm/inquiry_detail";
    }

    /**
     * 마케팅 페이지 (업체용)
     */
    @GetMapping("/marketing")
    public String marketing(Model model, HttpSession session) {
        Integer companyId = (Integer) session.getAttribute("companyId");
        model.addAttribute("totalImpressions", 125000);
        model.addAttribute("totalClicks", 8500);
        model.addAttribute("totalInquiries", 450);
        model.addAttribute("totalReservations", 180);
        model.addAttribute("conversionRate", 2.12);
        model.addAttribute("companyName", session.getAttribute("companyName"));
        model.addAttribute("companyId", companyId);

        List<Map<String, Object>> placements = getPlacementRequests();
        model.addAttribute("placements", placements);

        List<Map<String, Object>> coupons = getCoupons();
        model.addAttribute("coupons", coupons);

        model.addAttribute("sidebarType", "company");
        addAvatarInfo(model, companyId);
        return "crm/company_marketing";
    }

    /**
     * 도움말/고객센터 페이지 (업체용)
     */
    @GetMapping("/help-support")
    public String helpSupport(Model model, HttpSession session) {
        Integer companyId = (Integer) session.getAttribute("companyId");
        List<Map<String, Object>> requests = getHelpRequests();
        model.addAttribute("requests", requests);
        model.addAttribute("sidebarType", "company");
        model.addAttribute("companyName", session.getAttribute("companyName"));
        model.addAttribute("companyId", companyId);
        addAvatarInfo(model, companyId);
        return "crm/company_help_support";
    }

    /**
     * 도움말/고객센터 상세 페이지 (업체용)
     */
    @GetMapping("/help-support/detail/{id}")
    public String helpSupportDetail(@PathVariable("id") Long id, Model model, HttpSession session) {
        Integer companyId = (Integer) session.getAttribute("companyId");
        Map<String, Object> request = getHelpRequestById(id);
        model.addAttribute("request", request);
        model.addAttribute("sidebarType", "company");
        model.addAttribute("companyName", session.getAttribute("companyName"));
        model.addAttribute("companyId", companyId);
        addAvatarInfo(model, companyId);
        return "crm/company_help_support_detail";
    }
    
    /**
     * 아바타 정보 추가 헬퍼 메서드
     */
    private void addAvatarInfo(Model model, Integer companyId) {
        if (companyId != null) {
            Optional<CompanyUser> companyOpt = companyUserService.findById(companyId);
            boolean hasAvatar = companyOpt.isPresent() && 
                               companyOpt.get().getAvatarBlob() != null && 
                               companyOpt.get().getAvatarBlob().length > 0;
            model.addAttribute("hasAvatar", hasAvatar);
        } else {
            model.addAttribute("hasAvatar", false);
        }
    }

    // 업체 페이지용 데이터 생성 메서드들
    private Map<String, Object> getReservationChartData(CompanyUser company) {
        Map<String, Object> chartData = new HashMap<>();

        if (company == null) {
            // 업체 정보가 없을 경우 빈 데이터
            chartData.put("days", new ArrayList<>());
            chartData.put("reservations", new ArrayList<>());
            chartData.put("currentMonth", java.time.LocalDate.now().getMonthValue() + "월");
            return chartData;
        }

        // 현재 날짜 기준으로 이번 달의 일별 예약 데이터 조회
        java.time.LocalDate now = java.time.LocalDate.now();
        java.time.LocalDate startOfMonth = now.withDayOfMonth(1);
        java.time.LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        
        // 이번 달 시작과 끝의 LocalDateTime 생성
        java.time.LocalDateTime startDateTime = startOfMonth.atStartOfDay();
        java.time.LocalDateTime endDateTime = endOfMonth.atTime(23, 59, 59);
        
        System.out.println("=== 예약 차트 데이터 디버깅 ===");
        System.out.println("업체 ID: " + company.getCompanyId());
        System.out.println("업체명: " + company.getCompanyName());
        System.out.println("조회 기간 (created_at): " + startDateTime + " ~ " + endDateTime);
        
        // 이번 달의 모든 예약 조회 (created_at 기준)
        List<Reservation> monthReservations = reservationRepo.findByCompanyAndCreatedAtBetween(company, startDateTime, endDateTime);
        System.out.println("조회된 예약 수: " + monthReservations.size());
        
        // 일별 데이터 집계
        List<String> days = new ArrayList<>();
        List<Integer> dailyReservations = new ArrayList<>();
        
        // 이번 달의 모든 날짜에 대해 예약 수 집계 (1일부터 오늘까지)
        for (int day = 1; day <= now.getDayOfMonth(); day++) {
            java.time.LocalDate date = now.withDayOfMonth(day);
            days.add(day + "일");
            
            // 해당 날짜에 생성된 예약 수 계산 (created_at 기준)
            long count = monthReservations.stream()
                .filter(r -> r.getCreatedAt() != null && r.getCreatedAt().toLocalDate().equals(date))
                .count();
            
            if (count > 0) {
                System.out.println(day + "일 예약 생성 수: " + count);
            }
            
            dailyReservations.add((int) count);
        }
        
        System.out.println("최종 예약 데이터: " + dailyReservations);
        System.out.println("===============================");
        
        chartData.put("days", days);
        chartData.put("reservations", dailyReservations);
        chartData.put("currentMonth", now.getMonthValue() + "월");

        return chartData;
    }

    private List<Map<String, Object>> getCompanyReviews() {
        List<Map<String, Object>> reviews = new ArrayList<>();

        // 브이라인 리프팅 리뷰
        Map<String, Object> review1 = new HashMap<>();
        review1.put("name", "브이라인 리프팅");
        review1.put("productId", "PN0001265");
        review1.put("rating", "4.8★(288)");
        review1.put("price", "290,000원");
        review1.put("text", "한국어 리뷰 텍스트...\nEnglish review text...\n일본어 리뷰 텍스트...");
        reviews.add(review1);

        // 울쎄라피 프라임 리뷰
        Map<String, Object> review2 = new HashMap<>();
        review2.put("name", "울쎄라피 프라임");
        review2.put("productId", "PN0001265");
        review2.put("rating", "4.9★(140)");
        review2.put("price", "1,290,000원");
        review2.put("text", "한국어 리뷰 텍스트...\nEnglish review text...\n일본어 리뷰 텍스트...");
        reviews.add(review2);

        // 힝거 어깨필러 리뷰
        Map<String, Object> review3 = new HashMap<>();
        review3.put("name", "힝거 어깨필러");
        review3.put("productId", "PN0001265");
        review3.put("rating", "4.9★(49)");
        review3.put("price", "1,100,000원");
        review3.put("text", "일본어 리뷰 텍스트...\n한국어 리뷰 텍스트...");
        reviews.add(review3);

        return reviews;
    }

    private List<Map<String, Object>> getPopularEvents() {
        List<Map<String, Object>> events = new ArrayList<>();

        // 브이라인 리프팅 이벤트
        Map<String, Object> event1 = new HashMap<>();
        event1.put("name", "브이라인 리프팅");
        event1.put("price", "290,000원");
        event1.put("count", "553+");
        event1.put("trend", "up");
        events.add(event1);

        // 모공제로 모공주사 이벤트
        Map<String, Object> event2 = new HashMap<>();
        event2.put("name", "모공제로 모공주사");
        event2.put("price", "380,000원");
        event2.put("count", "200+");
        event2.put("trend", "down");
        events.add(event2);

        // 백옥같은 피부 레이저 이벤트
        Map<String, Object> event3 = new HashMap<>();
        event3.put("name", "백옥같은 피부 레이저");
        event3.put("price", "1,090,000원");
        event3.put("count", "110+");
        event3.put("trend", "down");
        events.add(event3);

        return events;
    }

    private List<Map<String, Object>> getMedicalServices() {
        List<Map<String, Object>> services = new ArrayList<>();

        // 브이라인 리프팅 1
        Map<String, Object> service1 = new HashMap<>();
        service1.put("productCode", "PN0001265");
        service1.put("name", "브이라인 리프팅");
        service1.put("rating", "4.8★(288)");
        service1.put("eventPrice", "이벤트 가 290,000원");
        service1.put("category", "lifting");
        services.add(service1);

        // 브이라인 리프팅 2
        Map<String, Object> service2 = new HashMap<>();
        service2.put("productCode", "PN0001265");
        service2.put("name", "브이라인 리프팅");
        service2.put("rating", "4.9★(140)");
        service2.put("eventPrice", "이벤트 가 1,290,000원");
        service2.put("category", "lifting");
        services.add(service2);

        // 브이라인 리프팅 3
        Map<String, Object> service3 = new HashMap<>();
        service3.put("productCode", "PN0001265");
        service3.put("name", "브이라인 리프팅");
        service3.put("rating", "4.9★(49)");
        service3.put("eventPrice", "이벤트 가 1,100,000원");
        service3.put("category", "lifting");
        services.add(service3);

        // 모공주사
        Map<String, Object> service4 = new HashMap<>();
        service4.put("productCode", "PN0001265");
        service4.put("name", "모공주사");
        service4.put("rating", "4.9★(128)");
        service4.put("eventPrice", "이벤트 가 380,000원");
        service4.put("category", "pore");
        services.add(service4);

        return services;
    }

    private List<Map<String, Object>> getInquiries() {
        List<Map<String, Object>> inquiries = new ArrayList<>();

        // 김민수 문의
        Map<String, Object> inquiry1 = new HashMap<>();
        inquiry1.put("id", 1);
        inquiry1.put("userName", "김민수");
        inquiry1.put("time", "2분 전");
        inquiry1.put("preview", "안녕하세요. 브이라인 리프팅에 대해 문의드립니다. 가격과 시술 시간이 궁금합니다.");
        inquiry1.put("status", "new");
        inquiry1.put("unreadCount", 3);
        inquiries.add(inquiry1);

        // 이영희 문의
        Map<String, Object> inquiry2 = new HashMap<>();
        inquiry2.put("id", 2);
        inquiry2.put("userName", "이영희");
        inquiry2.put("time", "1시간 전");
        inquiry2.put("preview", "예약 변경 요청드립니다. 다음 주로 연기하고 싶습니다.");
        inquiry2.put("status", "in-progress");
        inquiry2.put("unreadCount", 0);
        inquiries.add(inquiry2);

        // 박준호 문의
        Map<String, Object> inquiry3 = new HashMap<>();
        inquiry3.put("id", 3);
        inquiry3.put("userName", "박준호");
        inquiry3.put("time", "3시간 전");
        inquiry3.put("preview", "시술 후 주의사항에 대해 알려주세요.");
        inquiry3.put("status", "resolved");
        inquiry3.put("unreadCount", 0);
        inquiries.add(inquiry3);

        // 최수진 문의
        Map<String, Object> inquiry4 = new HashMap<>();
        inquiry4.put("id", 4);
        inquiry4.put("userName", "최수진");
        inquiry4.put("time", "1일 전");
        inquiry4.put("preview", "울쎄라피 시술에 대해 상담받고 싶습니다.");
        inquiry4.put("status", "new");
        inquiry4.put("unreadCount", 1);
        inquiries.add(inquiry4);

        // 정다은 문의
        Map<String, Object> inquiry5 = new HashMap<>();
        inquiry5.put("id", 5);
        inquiry5.put("userName", "정다은");
        inquiry5.put("time", "2일 전");
        inquiry5.put("preview", "예약 취소 요청드립니다.");
        inquiry5.put("status", "in-progress");
        inquiry5.put("unreadCount", 0);
        inquiries.add(inquiry5);

        return inquiries;
    }

    private List<Map<String, Object>> getInquiryReports() {
        List<Map<String, Object>> reports = new ArrayList<>();

        // 시술 후 부작용 문의
        Map<String, Object> report1 = new HashMap<>();
        report1.put("id", 1);
        report1.put("type", "inquiry");
        report1.put("title", "시술 후 부작용 문의");
        report1.put("reporterName", "김민수");
        report1.put("reporterPhone", "010-1234-5678");
        report1.put("reporterEmail", "kim@example.com");
        report1.put("description", "브이라인 리프팅 시술을 받은 후 얼굴이 부어오르고 통증이 있습니다. 정상적인 반응인지 확인하고 싶습니다.");
        report1.put("status", "pending");
        report1.put("priority", "high");
        report1.put("createdDate", "2024-01-15 14:30");
        reports.add(report1);

        // 의료진 태도 문제 신고
        Map<String, Object> report2 = new HashMap<>();
        report2.put("id", 2);
        report2.put("type", "report");
        report2.put("title", "의료진 태도 문제 신고");
        report2.put("reporterName", "이영희");
        report2.put("reporterPhone", "010-2345-6789");
        report2.put("reporterEmail", "lee@example.com");
        report2.put("description", "시술 중 의료진이 불친절하고 무성의한 태도로 시술을 진행했습니다. 환자에 대한 기본적인 예의가 부족했습니다.");
        report2.put("status", "processing");
        report2.put("priority", "medium");
        report2.put("createdDate", "2024-01-14 16:45");
        reports.add(report2);

        // 예약 변경 요청
        Map<String, Object> report3 = new HashMap<>();
        report3.put("id", 3);
        report3.put("type", "inquiry");
        report3.put("title", "예약 변경 요청");
        report3.put("reporterName", "박준호");
        report3.put("reporterPhone", "010-3456-7890");
        report3.put("reporterEmail", "park@example.com");
        report3.put("description", "개인 사정으로 인해 예약된 시술 일정을 다음 주로 변경하고 싶습니다. 가능한지 확인 부탁드립니다.");
        report3.put("status", "resolved");
        report3.put("priority", "low");
        report3.put("createdDate", "2024-01-13 10:20");
        reports.add(report3);

        // 시설 청결도 문제 신고
        Map<String, Object> report4 = new HashMap<>();
        report4.put("id", 4);
        report4.put("type", "report");
        report4.put("title", "시설 청결도 문제 신고");
        report4.put("reporterName", "최수진");
        report4.put("reporterPhone", "010-4567-8901");
        report4.put("reporterEmail", "choi@example.com");
        report4.put("description", "병원 내부 시설이 불결하고 위생상 문제가 있다고 생각됩니다. 정기적인 청소와 소독이 필요합니다.");
        report4.put("status", "rejected");
        report4.put("priority", "medium");
        report4.put("createdDate", "2024-01-12 09:15");
        reports.add(report4);

        // 시술 비용 환불 요청
        Map<String, Object> report5 = new HashMap<>();
        report5.put("id", 5);
        report5.put("type", "inquiry");
        report5.put("title", "시술 비용 환불 요청");
        report5.put("reporterName", "정다은");
        report5.put("reporterPhone", "010-5678-9012");
        report5.put("reporterEmail", "jung@example.com");
        report5.put("description", "시술 결과가 만족스럽지 않아 환불을 요청합니다. 계약서에 명시된 환불 정책에 따라 처리해주세요.");
        report5.put("status", "pending");
        report5.put("priority", "high");
        report5.put("createdDate", "2024-01-11 15:30");
        reports.add(report5);

        return reports;
    }

    private Map<String, Object> getInquiryReportById(Long id) {
        // 실제로는 DB에서 조회해야 함
        List<Map<String, Object>> reports = getInquiryReports();

        return reports.stream()
                .filter(report -> report.get("id").equals(id.intValue()))
                .findFirst()
                .orElse(new HashMap<>());
    }

    private List<Map<String, Object>> getHelpRequests() {
        List<Map<String, Object>> requests = new ArrayList<>();

        // 11월 프로모션 요청
        Map<String, Object> request1 = new HashMap<>();
        request1.put("id", 1);
        request1.put("title", "11월 프로모션 요청");
        request1.put("type", "프로모션");
        request1.put("status", "처리중");
        request1.put("priority", "일반");
        request1.put("createdDate", "2025-10-03");
        request1.put("answeredDate", null);
        request1.put("content", "수험생 타깃으로 11/19~27 이벤트 진행 예정입니다. 메인 페이지 노출 및 배너 등록을 요청드립니다.");
        request1.put("attachment", "event_banner.png");
        request1.put("adminAnswer", "검토 중입니다. 이벤트 상세 내용을 추가로 전달해주시면 신속히 처리하겠습니다.");
        requests.add(request1);

        // 악성 리뷰 신고
        Map<String, Object> request2 = new HashMap<>();
        request2.put("id", 2);
        request2.put("title", "악성 리뷰 신고");
        request2.put("type", "고객신고");
        request2.put("status", "완료");
        request2.put("priority", "긴급");
        request2.put("createdDate", "2025-09-30");
        request2.put("answeredDate", "2025-10-02");
        request2.put("content", "허위 사실로 작성된 리뷰로 업체 명예를 훼손하고 있습니다. 리뷰 삭제 요청드립니다.");
        request2.put("attachment", "review_screenshot.png");
        request2.put("adminAnswer", "해당 리뷰를 확인했으며, 커뮤니티 가이드라인 위반으로 삭제 조치하였습니다.");
        requests.add(request2);

        // 시스템 오류 문의
        Map<String, Object> request3 = new HashMap<>();
        request3.put("id", 3);
        request3.put("title", "예약 시스템 오류 문의");
        request3.put("type", "기술지원");
        request3.put("status", "완료");
        request3.put("priority", "긴급");
        request3.put("createdDate", "2025-09-28");
        request3.put("answeredDate", "2025-09-28");
        request3.put("content", "고객 예약이 정상적으로 등록되지 않는 오류가 발생하고 있습니다.");
        request3.put("attachment", null);
        request3.put("adminAnswer", "시스템 오류를 확인하여 긴급 수정하였습니다. 현재 정상 작동 중입니다.");
        requests.add(request3);

        // 정산 문의
        Map<String, Object> request4 = new HashMap<>();
        request4.put("id", 4);
        request4.put("title", "9월 정산 내역 확인");
        request4.put("type", "정산");
        request4.put("status", "완료");
        request4.put("priority", "일반");
        request4.put("createdDate", "2025-10-01");
        request4.put("answeredDate", "2025-10-01");
        request4.put("content", "9월 정산 내역이 예상과 다릅니다. 확인 부탁드립니다.");
        request4.put("attachment", null);
        request4.put("adminAnswer", "정산 내역을 확인하여 이메일로 상세 내역을 발송하였습니다.");
        requests.add(request4);

        // 계정 관리 문의
        Map<String, Object> request5 = new HashMap<>();
        request5.put("id", 5);
        request5.put("title", "관리자 계정 추가 요청");
        request5.put("type", "계정관리");
        request5.put("status", "반려");
        request5.put("priority", "일반");
        request5.put("createdDate", "2025-09-25");
        request5.put("answeredDate", "2025-09-26");
        request5.put("content", "직원 추가로 관리자 계정 1개 더 필요합니다.");
        request5.put("attachment", null);
        request5.put("adminAnswer", "현재 요금제에서는 관리자 계정 1개만 제공됩니다. 프리미엄 요금제로 업그레이드 시 추가 가능합니다.");
        requests.add(request5);

        return requests;
    }

    private Map<String, Object> getHelpRequestById(Long id) {
        // 실제로는 DB에서 조회해야 함
        List<Map<String, Object>> requests = getHelpRequests();

        return requests.stream()
                .filter(request -> request.get("id").equals(id.intValue()))
                .findFirst()
                .orElse(new HashMap<>());
    }

    private List<Map<String, Object>> getPlacementRequests() {
        List<Map<String, Object>> placements = new ArrayList<>();

        Map<String, Object> p1 = new HashMap<>();
        p1.put("id", 1);
        p1.put("title", "브이라인 리프팅 홈 배너");
        p1.put("slot", "홈 히어로");
        p1.put("target", "브이라인 리프팅");
        p1.put("startDate", "2025-10-01");
        p1.put("endDate", "2025-10-31");
        p1.put("status", "승인완료");
        p1.put("priority", "높음");
        p1.put("clicks", 2450);
        placements.add(p1);

        Map<String, Object> p2 = new HashMap<>();
        p2.put("id", 2);
        p2.put("title", "울쎄라피 카테고리 노출");
        p2.put("slot", "카테고리");
        p2.put("target", "울쎄라피 프라임");
        p2.put("startDate", "2025-10-10");
        p2.put("endDate", "2025-11-10");
        p2.put("status", "대기중");
        p2.put("priority", "보통");
        p2.put("clicks", 0);
        placements.add(p2);

        return placements;
    }

    private List<Map<String, Object>> getCoupons() {
        List<Map<String, Object>> coupons = new ArrayList<>();

        Map<String, Object> c1 = new HashMap<>();
        c1.put("id", 1);
        c1.put("name", "가을맞이 10% 할인");
        c1.put("type", "정율");
        c1.put("discount", "10%");
        c1.put("target", "전체 서비스");
        c1.put("validUntil", "2025-10-31");
        c1.put("issued", 500);
        c1.put("used", 87);
        c1.put("status", "진행중");
        coupons.add(c1);

        Map<String, Object> c2 = new HashMap<>();
        c2.put("id", 2);
        c2.put("name", "첫 방문 20,000원 할인");
        c2.put("type", "정액");
        c2.put("discount", "20,000원");
        c2.put("target", "첫 구매 고객");
        c2.put("validUntil", "2025-12-31");
        c2.put("issued", 1000);
        c2.put("used", 234);
        c2.put("status", "진행중");
        coupons.add(c2);

        return coupons;
    }
}