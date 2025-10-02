package com.example.ApiRound.crm.minggzz.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.ApiRound.crm.minggzz.Service.CompanyService;

@Controller
@RequestMapping("/company")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    /**
     * 업체 모드 대시보드 메인 페이지
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // 업체 통계 데이터
        Map<String, Object> companyStats = companyService.getCompanyStats();
        model.addAttribute("companyStats", companyStats);
        
        // 예약 현황 데이터
        List<Map<String, Object>> reservations = companyService.getReservationList();
        model.addAttribute("reservations", reservations);
        
        // 후기 현황 데이터
        List<Map<String, Object>> reviews = companyService.getReviewList();
        model.addAttribute("reviews", reviews);
        
        return "crm/company/dashboard";
    }
    
    /**
     * 의료 서비스 관리 페이지
     */
    @GetMapping("/medical-services")
    public String medicalServices(Model model) {
        // 의료 서비스 관리 로직 (추후 구현)
        return "crm/company/medical-services";
    }
    
    /**
     * 예약 관리 페이지
     */
    @GetMapping("/reservations")
    public String reservations(Model model) {
        // 예약 관리 로직 (추후 구현)
        return "crm/company/reservations";
    }
    
    /**
     * 후기 관리 페이지
     */
    @GetMapping("/reviews")
    public String reviews(Model model) {
        // 후기 관리 로직 (추후 구현)
        return "crm/company/reviews";
    }
    
    /**
     * 문의 & 채팅 페이지
     */
    @GetMapping("/inquiries")
    public String inquiries(Model model) {
        // 문의 & 채팅 관리 로직 (추후 구현)
        return "crm/company/inquiries";
    }
    
    /**
     * 마케팅 페이지
     */
    @GetMapping("/marketing")
    public String marketing(Model model) {
        // 마케팅 관리 로직 (추후 구현)
        return "crm/company/marketing";
    }
    
    /**
     * 리포트 & 통계 페이지
     */
    @GetMapping("/reports")
    public String reports(Model model) {
        // 리포트 & 통계 로직 (추후 구현)
        return "crm/company/reports";
    }
    
    /**
     * 업체 정보 수정 페이지
     */
    @GetMapping("/company-info")
    public String companyInfo(Model model) {
        // 업체 정보 수정 로직 (추후 구현)
        return "crm/company/company-info";
    }
    
    /**
     * 도움말/고객센터 페이지
     */
    @GetMapping("/help")
    public String help(Model model) {
        // 도움말/고객센터 로직 (추후 구현)
        return "crm/company/help";
    }
}
