package com.example.ApiRound.crm.minggzz;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/company")
public class CompanyController {

    /**
     * 업체 메인페이지 (힝거 피부과)
     */
    @GetMapping("")
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
        
        model.addAttribute("sidebarType", "company");
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
        
        model.addAttribute("sidebarType", "company");
        return "crm/medical_services";
    }

    /**
     * 이벤트 등록 페이지
     */
    @GetMapping("/event-registration")
    public String eventRegistration(Model model) {
        // 이벤트 등록 페이지 데이터 (실제로는 서비스에서 가져와야 함)
        // 예: 기존 태그 목록, 카테고리 목록 등
        model.addAttribute("sidebarType", "company");
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
        
        model.addAttribute("sidebarType", "company");
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
        model.addAttribute("sidebarType", "company");
        
        return "crm/inquiry_report";
    }

    /**
     * 문의/신고 상세 페이지 (업체용)
     */
    @GetMapping("/inquiry-report/detail/{id}")
    public String inquiryReportDetail(@PathVariable("id") Long id, Model model) {
        // 문의/신고 상세 데이터 (실제로는 서비스에서 가져와야 함)
        Map<String, Object> report = getInquiryReportById(id);
        model.addAttribute("report", report);
        model.addAttribute("sidebarType", "company");
        
        return "crm/inquiry_detail";
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

    private Map<String, Object> getInquiryReportById(Long id) {
        // 실제로는 DB에서 조회해야 함
        List<Map<String, Object>> reports = getInquiryReports();
        
        return reports.stream()
                .filter(report -> report.get("id").equals(id.intValue()))
                .findFirst()
                .orElse(new HashMap<>());
    }
}
