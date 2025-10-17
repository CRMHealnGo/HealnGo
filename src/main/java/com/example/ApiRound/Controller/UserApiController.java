package com.example.ApiRound.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ApiRound.dto.SocialUserDTO;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class UserApiController {
    
    /**
     * 현재 로그인한 사용자 정보 조회
     * GET /api/current-user
     */
    @GetMapping("/current-user")
    public ResponseEntity<Map<String, Object>> getCurrentUser(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        SocialUserDTO user = getLoginUser(session);
        
        if (user == null) {
            response.put("error", "로그인이 필요합니다.");
            return ResponseEntity.status(401).body(response);
        }
        
        response.put("userId", user.getId());
        response.put("userName", user.getName());
        response.put("email", user.getEmail());
        response.put("provider", user.getProvider());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 로그인 상태 확인
     * GET /api/check-login
     */
    @GetMapping("/check-login")
    public ResponseEntity<Map<String, Object>> checkLogin(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        SocialUserDTO user = getLoginUser(session);
        response.put("isLoggedIn", user != null);
        
        if (user != null) {
            response.put("userId", user.getId());
            response.put("userName", user.getName());
        }
        
        return ResponseEntity.ok(response);
    }
    
    // 로그인 사용자 가져오기 (헬퍼 메서드)
    private SocialUserDTO getLoginUser(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return null;
        }
        
        // 세션에서 사용자 정보로 DTO 생성
        SocialUserDTO user = new SocialUserDTO();
        user.setId(userId.longValue()); // Integer를 Long으로 변환
        user.setEmail((String) session.getAttribute("userEmail"));
        user.setName((String) session.getAttribute("userName"));
        user.setProvider((String) session.getAttribute("provider"));
        return user;
    }
}

