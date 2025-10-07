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
}
