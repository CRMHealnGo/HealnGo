package com.example.ApiRound.crm.minggzz.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class CompanyServiceImpl implements CompanyService {

    @Override
    public Map<String, Object> getCompanyStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalReservations", 156);
        stats.put("todayReservations", 8);
        stats.put("pendingReservations", 12);
        stats.put("completedReservations", 144);
        stats.put("averageRating", 4.7);
        stats.put("totalReviews", 89);
        stats.put("newReviews", 3);
        return stats;
    }

    @Override
    public List<Map<String, Object>> getReservationList() {
        List<Map<String, Object>> reservations = new ArrayList<>();
        
        Map<String, Object> reservation1 = new HashMap<>();
        reservation1.put("id", "R001");
        reservation1.put("customerName", "김민수");
        reservation1.put("service", "성형외과 상담");
        reservation1.put("date", "2025-10-01");
        reservation1.put("time", "14:00");
        reservation1.put("status", "확정");
        reservations.add(reservation1);
        
        Map<String, Object> reservation2 = new HashMap<>();
        reservation2.put("id", "R002");
        reservation2.put("customerName", "이영희");
        reservation2.put("service", "피부과 치료");
        reservation2.put("date", "2025-10-01");
        reservation2.put("time", "16:30");
        reservation2.put("status", "대기");
        reservations.add(reservation2);
        
        Map<String, Object> reservation3 = new HashMap<>();
        reservation3.put("id", "R003");
        reservation3.put("customerName", "박지영");
        reservation3.put("service", "치과 검진");
        reservation3.put("date", "2025-10-02");
        reservation3.put("time", "10:00");
        reservation3.put("status", "완료");
        reservations.add(reservation3);
        
        return reservations;
    }

    @Override
    public List<Map<String, Object>> getReviewList() {
        List<Map<String, Object>> reviews = new ArrayList<>();
        
        Map<String, Object> review1 = new HashMap<>();
        review1.put("id", "RV001");
        review1.put("customerName", "최수진");
        review1.put("rating", 5);
        review1.put("content", "정말 만족스러운 서비스였습니다!");
        review1.put("date", "2025-09-28");
        reviews.add(review1);
        
        Map<String, Object> review2 = new HashMap<>();
        review2.put("id", "RV002");
        review2.put("customerName", "정민호");
        review2.put("rating", 4);
        review2.put("content", "좋은 경험이었습니다.");
        review2.put("date", "2025-09-27");
        reviews.add(review2);
        
        Map<String, Object> review3 = new HashMap<>();
        review3.put("id", "RV003");
        review3.put("customerName", "한소영");
        review3.put("rating", 5);
        review3.put("content", "친절하고 전문적인 치료 감사합니다.");
        review3.put("date", "2025-09-26");
        reviews.add(review3);
        
        return reviews;
    }

    @Override
    public List<Map<String, Object>> getMedicalServices() {
        List<Map<String, Object>> services = new ArrayList<>();
        
        Map<String, Object> service1 = new HashMap<>();
        service1.put("name", "성형외과");
        service1.put("description", "코성형, 눈성형, 가슴성형 등");
        service1.put("price", "상담 후 결정");
        service1.put("status", "운영중");
        services.add(service1);
        
        Map<String, Object> service2 = new HashMap<>();
        service2.put("name", "피부과");
        service2.put("description", "레이저치료, 보톡스, 필러 등");
        service2.put("price", "시술별 상이");
        service2.put("status", "운영중");
        services.add(service2);
        
        Map<String, Object> service3 = new HashMap<>();
        service3.put("name", "치과");
        service3.put("description", "임플란트, 교정, 심미치료 등");
        service3.put("price", "진료별 상이");
        service3.put("status", "운영중");
        services.add(service3);
        
        return services;
    }

    @Override
    public Map<String, Object> getMarketingData() {
        Map<String, Object> marketing = new HashMap<>();
        marketing.put("totalViews", 1250);
        marketing.put("totalClicks", 89);
        marketing.put("conversionRate", 7.1);
        marketing.put("adSpend", 500000);
        marketing.put("roi", 2.3);
        return marketing;
    }

    @Override
    public Map<String, Object> getReportData() {
        Map<String, Object> report = new HashMap<>();
        report.put("monthlyRevenue", 15000000);
        report.put("monthlyGrowth", 12.5);
        report.put("customerSatisfaction", 4.7);
        report.put("repeatCustomers", 68);
        return report;
    }
}
