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
}
