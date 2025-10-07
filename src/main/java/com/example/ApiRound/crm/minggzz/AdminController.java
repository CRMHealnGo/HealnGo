package com.example.ApiRound.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    /**
     * 관리자 대시보드 메인 페이지
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // 대시보드 통계 데이터 (실제로는 서비스에서 가져와야 함)
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", 1250);
        stats.put("totalCompanies", 45);
        stats.put("totalReservations", 320);
        stats.put("totalRevenue", 15000000);
        
        model.addAttribute("stats", stats);
        
        // 최근 활동 데이터
        List<Map<String, Object>> recentActivities = getRecentActivities();
        model.addAttribute("recentActivities", recentActivities);
        
        // 차트 데이터
        Map<String, Object> chartData = getChartData();
        model.addAttribute("chartData", chartData);
        
        return "admin/admin";
    }

    /**
     * 사용자 관리 페이지
     */
    @GetMapping("/users")
    public String users(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search,
            Model model) {
        
        // 사용자 목록 데이터 (실제로는 서비스에서 가져와야 함)
        List<Map<String, Object>> users = getUsers(page, size, search);
        model.addAttribute("users", users);
        
        // 페이지네이션 정보
        int totalUsers = 1250; // 실제로는 DB에서 조회
        int totalPages = (int) Math.ceil((double) totalUsers / size);
        
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("search", search);
        
        return "admin/users";
    }

    /**
     * 업체 관리 페이지
     */
    @GetMapping("/companies")
    public String companies(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search,
            Model model) {
        
        // 업체 목록 데이터 (실제로는 서비스에서 가져와야 함)
        List<Map<String, Object>> companies = getCompanies(page, size, search);
        model.addAttribute("companies", companies);
        
        // 페이지네이션 정보
        int totalCompanies = 45; // 실제로는 DB에서 조회
        int totalPages = (int) Math.ceil((double) totalCompanies / size);
        
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalCompanies", totalCompanies);
        model.addAttribute("search", search);
        
        return "admin/companies";
    }

    /**
     * 예약 관리 페이지
     */
    @GetMapping("/reservations")
    public String reservations(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search,
            Model model) {
        
        // 예약 목록 데이터 (실제로는 서비스에서 가져와야 함)
        List<Map<String, Object>> reservations = getReservations(page, size, search);
        model.addAttribute("reservations", reservations);
        
        // 페이지네이션 정보
        int totalReservations = 320; // 실제로는 DB에서 조회
        int totalPages = (int) Math.ceil((double) totalReservations / size);
        
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalReservations", totalReservations);
        model.addAttribute("search", search);
        
        return "admin/reservations";
    }

    /**
     * 업체 메인페이지 (힝거 피부과)
     */
    @GetMapping("/company")
    public String company(Model model) {
        // 업체 대시보드 데이터 (실제로는 서비스에서 가져와야 함)
        Map<String, Object> companyStats = new HashMap<>();
        companyStats.put("foreignTouristIncrease", "15% 증가");
        companyStats.put("koreanTouristIncrease", "5% 증가");
        companyStats.put("newEventProducts", "4건");
        
        model.addAttribute("companyStats", companyStats);
        
        // 예약 차트 데이터
        Map<String, Object> reservationChartData = getReservationChartData();
        model.addAttribute("reservationChartData", reservationChartData);
        
        // 후기 데이터
        List<Map<String, Object>> reviews = getCompanyReviews();
        model.addAttribute("reviews", reviews);
        
        // 인기 이벤트 데이터
        List<Map<String, Object>> popularEvents = getPopularEvents();
        model.addAttribute("popularEvents", popularEvents);
        
        return "crm/company";
    }

    /**
     * 의료 서비스 관리 페이지
     */
    @GetMapping("/medical-services")
    public String medicalServices(Model model) {
        // 의료 서비스 데이터 (실제로는 서비스에서 가져와야 함)
        List<Map<String, Object>> medicalServices = getMedicalServices();
        model.addAttribute("medicalServices", medicalServices);
        
        return "crm/medical_services";
    }

    /**
     * 이벤트 등록 페이지
     */
    @GetMapping("/event-registration")
    public String eventRegistration(Model model) {
        // 이벤트 등록 페이지 데이터 (실제로는 서비스에서 가져와야 함)
        // 예: 기존 태그 목록, 카테고리 목록 등
        return "crm/event_registration";
    }

    /**
     * 문의 & 채팅 페이지
     */
    @GetMapping("/inquiry-chat")
    public String inquiryChat(Model model) {
        // 문의 목록 데이터 (실제로는 서비스에서 가져와야 함)
        List<Map<String, Object>> inquiries = getInquiries();
        model.addAttribute("inquiries", inquiries);
        
        return "crm/inquiry_chat";
    }

    /**
     * 문의/신고 접수 페이지
     */
    @GetMapping("/inquiry-report")
    public String inquiryReport(Model model) {
        // 문의/신고 목록 데이터 (실제로는 서비스에서 가져와야 함)
        List<Map<String, Object>> reports = getInquiryReports();
        model.addAttribute("reports", reports);
        
        return "crm/inquiry_report";
    }

    // 임시 데이터 생성 메서드들 (실제로는 서비스에서 구현)
    private List<Map<String, Object>> getRecentActivities() {
        List<Map<String, Object>> activities = new ArrayList<>();
        
        Map<String, Object> activity1 = new HashMap<>();
        activity1.put("type", "user_registration");
        activity1.put("message", "새 사용자가 가입했습니다");
        activity1.put("time", "2분 전");
        activity1.put("icon", "fas fa-user-plus");
        activities.add(activity1);
        
        Map<String, Object> activity2 = new HashMap<>();
        activity2.put("type", "reservation");
        activity2.put("message", "새로운 예약이 생성되었습니다");
        activity2.put("time", "5분 전");
        activity2.put("icon", "fas fa-calendar-plus");
        activities.add(activity2);
        
        Map<String, Object> activity3 = new HashMap<>();
        activity3.put("type", "company_approval");
        activity3.put("message", "업체 승인이 완료되었습니다");
        activity3.put("time", "10분 전");
        activity3.put("icon", "fas fa-building");
        activities.add(activity3);
        
        return activities;
    }

    private Map<String, Object> getChartData() {
        Map<String, Object> chartData = new HashMap<>();
        
        // 월별 사용자 증가 데이터
        List<Integer> userGrowth = List.of(120, 150, 180, 200, 220, 250);
        chartData.put("userGrowth", userGrowth);
        
        // 월별 예약 데이터
        List<Integer> reservationData = List.of(45, 60, 75, 90, 85, 95);
        chartData.put("reservations", reservationData);
        
        return chartData;
    }

    private List<Map<String, Object>> getUsers(int page, int size, String search) {
        List<Map<String, Object>> users = new ArrayList<>();
        
        // 임시 사용자 데이터
        for (int i = 1; i <= size; i++) {
            Map<String, Object> user = new HashMap<>();
            user.put("id", (page - 1) * size + i);
            user.put("name", "사용자" + ((page - 1) * size + i));
            user.put("email", "user" + ((page - 1) * size + i) + "@example.com");
            user.put("joinDate", "2024-01-" + String.format("%02d", i % 28 + 1));
            user.put("status", i % 3 == 0 ? "비활성" : "활성");
            user.put("reportCount", i % 5);
            users.add(user);
        }
        
        return users;
    }

    private List<Map<String, Object>> getCompanies(int page, int size, String search) {
        List<Map<String, Object>> companies = new ArrayList<>();
        
        // 임시 업체 데이터
        String[] companyTypes = {"병원", "미용실", "마사지", "치과", "한의원"};
        
        for (int i = 1; i <= size; i++) {
            Map<String, Object> company = new HashMap<>();
            company.put("id", (page - 1) * size + i);
            company.put("name", companyTypes[i % companyTypes.length] + " " + i);
            company.put("owner", "사업자" + i);
            company.put("phone", "010-1234-" + String.format("%04d", i));
            company.put("status", i % 4 == 0 ? "승인대기" : "승인완료");
            company.put("joinDate", "2024-01-" + String.format("%02d", i % 28 + 1));
            companies.add(company);
        }
        
        return companies;
    }

    private List<Map<String, Object>> getReservations(int page, int size, String search) {
        List<Map<String, Object>> reservations = new ArrayList<>();
        
        // 임시 예약 데이터
        String[] services = {"진료", "미용", "마사지", "치과진료", "한의진료"};
        
        for (int i = 1; i <= size; i++) {
            Map<String, Object> reservation = new HashMap<>();
            reservation.put("id", (page - 1) * size + i);
            reservation.put("userName", "고객" + i);
            reservation.put("companyName", "업체" + i);
            reservation.put("service", services[i % services.length]);
            reservation.put("date", "2024-01-" + String.format("%02d", i % 28 + 1));
            reservation.put("time", String.format("%02d:00", 9 + (i % 8)));
            reservation.put("status", i % 3 == 0 ? "완료" : i % 3 == 1 ? "예약" : "취소");
            reservation.put("amount", 50000 + (i * 10000));
            reservations.add(reservation);
        }
        
        return reservations;
    }

    // 업체 페이지용 데이터 생성 메서드들
    private Map<String, Object> getReservationChartData() {
        Map<String, Object> chartData = new HashMap<>();
        
        // 외국인 사용객 데이터
        List<Integer> foreignUsers = List.of(36, 24, 30, 44, 58, 77, 89);
        chartData.put("foreignUsers", foreignUsers);
        
        // 한국인 사용객 데이터
        List<Integer> koreanUsers = List.of(50, 60, 52, 59, 62, 80, 70);
        chartData.put("koreanUsers", koreanUsers);
        
        // 월별 라벨
        List<String> months = List.of("4월", "5월", "6월", "7월", "8월", "9월", "10월");
        chartData.put("months", months);
        
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
}
