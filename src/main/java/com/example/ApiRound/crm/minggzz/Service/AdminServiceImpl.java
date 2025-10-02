package com.example.ApiRound.crm.minggzz.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.ApiRound.crm.minggzz.mapper.AdminMapper;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;

    public AdminServiceImpl(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }

    @Override
    public Map<String, Object> getUserStats() {
        // 기본 데이터 반환 (DB 연결 없이도 작동)
        Map<String, Object> userStats = new HashMap<>();
        userStats.put("totalUsers", 1250);
        userStats.put("newUsers", 45);
        userStats.put("activeUsers", 890);
        userStats.put("foreignUsers", 320);
        userStats.put("koreanUsers", 930);
        
        // 월별 사용자 데이터 (외국인)
        List<Integer> foreignUserData = Arrays.asList(36, 24, 30, 55, 73, 95, 89);
        userStats.put("foreignUserData", foreignUserData);
        
        // 월별 사용자 데이터 (한국인)
        List<Integer> koreanUserData = Arrays.asList(50, 60, 52, 44, 59, 80, 70);
        userStats.put("koreanUserData", koreanUserData);
        
        // 월별 라벨
        List<String> monthLabels = Arrays.asList("4월", "5월", "6월", "7월", "8월", "9월", "10월");
        userStats.put("monthLabels", monthLabels);
        
        return userStats;
    }

    @Override
    public List<Map<String, Object>> getCompanyList() {
        List<Map<String, Object>> companies = new ArrayList<>();
        
        Map<String, Object> company1 = new HashMap<>();
        company1.put("id", "PN0001265");
        company1.put("name", "(주)말랑핑업체");
        company1.put("contractDate", "Sep 12, 2021");
        company1.put("rating", 4.8);
        company1.put("ratingChange", "up");
        company1.put("reviewCount", 288);
        company1.put("packages", 34);
        company1.put("planners", 13);
        company1.put("reservations", 2);
        companies.add(company1);
        
        Map<String, Object> company2 = new HashMap<>();
        company2.put("id", "PN0001221");
        company2.put("name", "허나허나업체");
        company2.put("contractDate", "Sep 10, 2020");
        company2.put("rating", 4.7);
        company2.put("ratingChange", "up");
        company2.put("reviewCount", 312);
        company2.put("packages", 50);
        company2.put("planners", 24);
        company2.put("reservations", 1);
        companies.add(company2);
        
        Map<String, Object> company3 = new HashMap<>();
        company3.put("id", "PN0001290");
        company3.put("name", "요요업체");
        company3.put("contractDate", "May 28, 2024");
        company3.put("rating", 4.2);
        company3.put("ratingChange", "down");
        company3.put("reviewCount", 162);
        company3.put("packages", 23);
        company3.put("planners", 20);
        company3.put("reservations", 5);
        companies.add(company3);
        
        return companies;
    }

    @Override
    public List<Map<String, Object>> getPopularPackages() {
        List<Map<String, Object>> popularPackages = new ArrayList<>();
        
        Map<String, Object> package1 = new HashMap<>();
        package1.put("name", "화이트닝 패키지");
        package1.put("company", "하얀만두 업체");
        package1.put("count", 553);
        package1.put("trend", "up");
        popularPackages.add(package1);
        
        Map<String, Object> package2 = new HashMap<>();
        package2.put("name", "다이어트 패키지");
        package2.put("company", "노란리본 업체");
        package2.put("count", 260);
        package2.put("trend", "down");
        popularPackages.add(package2);
        
        Map<String, Object> package3 = new HashMap<>();
        package3.put("name", "여름 패키지");
        package3.put("company", "핫핫 업체");
        package3.put("count", 200);
        package3.put("trend", "down");
        popularPackages.add(package3);
        
        return popularPackages;
    }

    @Override
    public List<Map<String, Object>> getScheduleList() {
        List<Map<String, Object>> schedules = new ArrayList<>();
        
        Map<String, Object> schedule1 = new HashMap<>();
        schedule1.put("date", "10/13");
        schedule1.put("events", Arrays.asList(
            "밝은미소 패키지 예약",
            "반짝업체 입점",
            "반짝업체 광고일(13~16)",
            "공지사항"
        ));
        schedule1.put("details", Arrays.asList(
            "-1번 공지 올라감.",
            "-고객 신고 처리 공지 올라감"
        ));
        schedules.add(schedule1);
        
        Map<String, Object> schedule2 = new HashMap<>();
        schedule2.put("date", "10/16");
        schedule2.put("events", Arrays.asList(
            "입점 업체 결제처리",
            "해맑업체 입점",
            "해맑업체 광고일(16~19)",
            "공지사항"
        ));
        schedule2.put("details", Arrays.asList(
            "-고객 신고 처리 공지 올라감"
        ));
        schedules.add(schedule2);
        
        return schedules;
    }

    @Override
    public Map<String, Object> getMonthlyUserData() {
        Map<String, Object> monthlyData = new HashMap<>();
        
        // 월별 사용자 데이터 (외국인)
        List<Integer> foreignUserData = Arrays.asList(36, 24, 30, 55, 73, 95, 89);
        monthlyData.put("foreignUserData", foreignUserData);
        
        // 월별 사용자 데이터 (한국인)
        List<Integer> koreanUserData = Arrays.asList(50, 60, 52, 44, 59, 80, 70);
        monthlyData.put("koreanUserData", koreanUserData);
        
        // 월별 라벨
        List<String> monthLabels = Arrays.asList("4월", "5월", "6월", "7월", "8월", "9월", "10월");
        monthlyData.put("monthLabels", monthLabels);
        
        return monthlyData;
    }
}
