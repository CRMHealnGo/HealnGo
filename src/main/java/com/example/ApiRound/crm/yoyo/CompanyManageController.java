package com.example.ApiRound.crm.yoyo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
@RequestMapping("/company")
public class CompanyManageController {

    @GetMapping("/review")
    public String companyReview(Model model) {
        // 리뷰 통계 데이터 (실제로는 서비스에서 가져와야 함)
        model.addAttribute("totalReviews", 26);
        model.addAttribute("pendingReplies", 3);
        model.addAttribute("completedReplies", 16);
        model.addAttribute("averageRating", 4.7);
        model.addAttribute("sidebarType", "company");
        
        return "crm/company_review";
    }

    @GetMapping("/report")
    public String companyReport(Model model) {
        // 리포트 데이터 (실제로는 서비스에서 가져와야 함)
        
        // 일일 매출 리포트 데이터
        List<Map<String, Object>> dailySalesData = getDailySalesData();
        model.addAttribute("dailySalesData", dailySalesData);
        
        // 핵심 지표 데이터
        Map<String, Object> keyIndicators = getKeyIndicators();
        model.addAttribute("keyIndicators", keyIndicators);
        
        // 최근 활동 데이터
        List<Map<String, Object>> recentActivities = getRecentActivities();
        model.addAttribute("recentActivities", recentActivities);
        
        // 리포트 카드 데이터
        List<Map<String, Object>> reportCards = getReportCards();
        model.addAttribute("reportCards", reportCards);
        
        model.addAttribute("sidebarType", "company");
        
        return "crm/company_report";
    }

    // 임시 데이터 생성 메서드들 (실제로는 서비스에서 구현)
    private List<Map<String, Object>> getDailySalesData() {
        List<Map<String, Object>> data = new ArrayList<>();
        
        String[] months = {"4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월"};
        int[] koreanUsers = {50, 60, 52, 44, 58, 62, 80, 70};
        int[] foreignUsers = {36, 24, 30, 59, 73, 77, 95, 89};
        
        for (int i = 0; i < months.length; i++) {
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", months[i]);
            monthData.put("koreanUsers", koreanUsers[i]);
            monthData.put("foreignUsers", foreignUsers[i]);
            data.add(monthData);
        }
        
        return data;
    }

    private Map<String, Object> getKeyIndicators() {
        Map<String, Object> indicators = new HashMap<>();
        indicators.put("totalReservations", 1230);
        indicators.put("reservationTrend", "low");
        indicators.put("repeatVisits", 450);
        indicators.put("averageSatisfaction", 4.8);
        return indicators;
    }

    private List<Map<String, Object>> getRecentActivities() {
        List<Map<String, Object>> activities = new ArrayList<>();
        
        Map<String, Object> activity1 = new HashMap<>();
        activity1.put("icon", "fas fa-check-circle");
        activity1.put("title", "신규 상담 완료");
        activity1.put("time", "2시간 전");
        activity1.put("color", "green");
        activities.add(activity1);
        
        Map<String, Object> activity2 = new HashMap<>();
        activity2.put("icon", "fas fa-user-plus");
        activity2.put("title", "환자 3명 등록");
        activity2.put("time", "5시간 전");
        activity2.put("color", "blue");
        activities.add(activity2);
        
        Map<String, Object> activity3 = new HashMap<>();
        activity3.put("icon", "fas fa-exclamation-triangle");
        activity3.put("title", "예약 변경 요청");
        activity3.put("time", "1월 전");
        activity3.put("color", "orange");
        activities.add(activity3);
        
        return activities;
    }

    private List<Map<String, Object>> getReportCards() {
        List<Map<String, Object>> cards = new ArrayList<>();
        
        Map<String, Object> card1 = new HashMap<>();
        card1.put("title", "서비스별 예약률");
        card1.put("pages", "5 pages");
        card1.put("icon", "fas fa-folder");
        card1.put("color", "yellow");
        cards.add(card1);
        
        Map<String, Object> card2 = new HashMap<>();
        card2.put("title", "서비스별 매출 분석");
        card2.put("pages", "8 pages");
        card2.put("icon", "fas fa-folder");
        card2.put("color", "green");
        cards.add(card2);
        
        Map<String, Object> card3 = new HashMap<>();
        card3.put("title", "월별 방문자 수");
        card3.put("pages", "2 pages");
        card3.put("icon", "fas fa-folder");
        card3.put("color", "blue");
        cards.add(card3);
        
        Map<String, Object> card4 = new HashMap<>();
        card4.put("title", "환자 국가 분포");
        card4.put("pages", "5 pages");
        card4.put("icon", "fas fa-folder");
        card4.put("color", "purple");
        cards.add(card4);
        
        return cards;
    }
}
