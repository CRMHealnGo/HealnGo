package com.example.ApiRound.crm.minggzz.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminMapper {
    
    /**
     * 사용자 통계 조회 (기본 데이터 반환)
     */
    @Select("SELECT 1250 as totalUsers, 45 as newUsers, 890 as activeUsers")
    Map<String, Object> getUserStatistics();
    
    /**
     * 외국인 사용자 월별 데이터 (기본 데이터 반환)
     */
    @Select("SELECT '4월' as month, 36 as count UNION ALL " +
            "SELECT '5월' as month, 24 as count UNION ALL " +
            "SELECT '6월' as month, 30 as count UNION ALL " +
            "SELECT '7월' as month, 55 as count UNION ALL " +
            "SELECT '8월' as month, 73 as count UNION ALL " +
            "SELECT '9월' as month, 95 as count UNION ALL " +
            "SELECT '10월' as month, 89 as count")
    List<Map<String, Object>> getForeignUserMonthlyData();
    
    /**
     * 한국인 사용자 월별 데이터 (기본 데이터 반환)
     */
    @Select("SELECT '4월' as month, 50 as count UNION ALL " +
            "SELECT '5월' as month, 60 as count UNION ALL " +
            "SELECT '6월' as month, 52 as count UNION ALL " +
            "SELECT '7월' as month, 44 as count UNION ALL " +
            "SELECT '8월' as month, 59 as count UNION ALL " +
            "SELECT '9월' as month, 80 as count UNION ALL " +
            "SELECT '10월' as month, 70 as count")
    List<Map<String, Object>> getKoreanUserMonthlyData();
}
