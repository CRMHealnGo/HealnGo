package com.example.ApiRound.crm.minggzz.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.ApiRound.crm.minggzz.Service.AdminService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * 관리자 대시보드 메인 페이지
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // 사용자 통계 데이터
        Map<String, Object> userStats = adminService.getUserStats();
        model.addAttribute("userStats", userStats);
        
        // 업체 관리 데이터
        List<Map<String, Object>> companies = adminService.getCompanyList();
        model.addAttribute("companies", companies);
        
        // 인기 패키지 데이터
        List<Map<String, Object>> popularPackages = adminService.getPopularPackages();
        model.addAttribute("popularPackages", popularPackages);
        
        // 스케줄 데이터
        List<Map<String, Object>> schedules = adminService.getScheduleList();
        model.addAttribute("schedules", schedules);
        
        return "crm/admin/dashboard";
    }
    
    /**
     * 사용자 관리 페이지
     */
    @GetMapping("/users")
    public String userManagement(Model model) {
        // 사용자 관리 로직 (추후 구현)
        return "crm/admin/users";
    }
    
    /**
     * 업체 관리 페이지
     */
    @GetMapping("/companies")
    public String companyManagement(Model model) {
        // 업체 관리 로직 (추후 구현)
        return "crm/admin/companies";
    }
    
    /**
     * 예약 관리 페이지
     */
    @GetMapping("/reservations")
    public String reservationManagement(Model model) {
        // 예약 관리 로직 (추후 구현)
        return "crm/admin/reservations";
    }
    
    /**
     * 리포트 & 통계 페이지
     */
    @GetMapping("/reports")
    public String reports(Model model) {
        // 리포트 로직 (추후 구현)
        return "crm/admin/reports";
    }
    
    /**
     * 공지사항 & 알림 관리 페이지
     */
    @GetMapping("/notices")
    public String notices(Model model) {
        // 공지사항 관리 로직 (추후 구현)
        return "crm/admin/notices";
    }
    
    /**
     * 문의/신고 접수 페이지
     */
    @GetMapping("/inquiries")
    public String inquiries(Model model) {
        // 문의/신고 관리 로직 (추후 구현)
        return "crm/admin/inquiries";
    }
    
    /**
     * 대시보드 데이터 API (AJAX용)
     */
    @GetMapping("/api/dashboard-data")
    public String getDashboardData(Model model) {
        // 실시간 데이터 조회 로직 (추후 구현)
        return "crm/admin/dashboard";
    }
}
