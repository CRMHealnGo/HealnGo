package com.example.ApiRound.crm.hyeonah.Service;

import java.util.List;
import java.util.Map;

public interface ManagerUserService {
    
    /**
     * 사용자 목록 조회 (페이지네이션)
     */
    List<Map<String, Object>> getUserList(int pageNo, int amount, String search);
    
    /**
     * 전체 사용자 수 조회
     */
    int getTotalUserCount(String search);
}
