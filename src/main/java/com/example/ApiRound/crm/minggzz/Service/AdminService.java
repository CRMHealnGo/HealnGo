package com.example.ApiRound.crm.minggzz.Service;

import java.util.List;
import java.util.Map;

public interface AdminService {
    
    /**
     * 사용자 통계 데이터 조회
     */
    Map<String, Object> getUserStats();
    
    /**
     * 업체 관리 데이터 조회
     */
    List<Map<String, Object>> getCompanyList();
    
    /**
     * 인기 패키지 데이터 조회
     */
    List<Map<String, Object>> getPopularPackages();
    
    /**
     * 스케줄 데이터 조회
     */
    List<Map<String, Object>> getScheduleList();
    
    /**
     * 월별 사용자 데이터 조회
     */
    Map<String, Object> getMonthlyUserData();
}
