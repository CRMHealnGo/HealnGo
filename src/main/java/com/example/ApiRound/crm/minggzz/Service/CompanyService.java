package com.example.ApiRound.crm.minggzz.Service;

import java.util.List;
import java.util.Map;

public interface CompanyService {
    
    /**
     * 업체 통계 데이터 조회
     */
    Map<String, Object> getCompanyStats();
    
    /**
     * 예약 현황 데이터 조회
     */
    List<Map<String, Object>> getReservationList();
    
    /**
     * 후기 현황 데이터 조회
     */
    List<Map<String, Object>> getReviewList();
    
    /**
     * 의료 서비스 데이터 조회
     */
    List<Map<String, Object>> getMedicalServices();
    
    /**
     * 마케팅 데이터 조회
     */
    Map<String, Object> getMarketingData();
    
    /**
     * 리포트 데이터 조회
     */
    Map<String, Object> getReportData();
}
