package com.example.ApiRound.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ApiRound.dto.SocialUserDTO;
import com.example.ApiRound.crm.hyeonah.Repository.SocialUsersRepository;
import com.example.ApiRound.crm.hyeonah.entity.SocialUsers;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserApiController {
    
    private final SocialUsersRepository socialUsersRepository;
    
    public UserApiController(SocialUsersRepository socialUsersRepository) {
        this.socialUsersRepository = socialUsersRepository;
    }
    
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
    
    // 로그인 사용자 가져오기 (헬퍼 메서드) - 상태 검증 포함
    private SocialUserDTO getLoginUser(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return null;
        }
        
        try {
            // DB에서 실제 사용자 정보 조회 및 상태 검증
            Optional<SocialUsers> userOpt = socialUsersRepository.findById(userId);
            if (userOpt.isEmpty()) {
                // 사용자가 DB에서 삭제됨 - 세션 무효화
                session.invalidate();
                return null;
            }
            
            SocialUsers user = userOpt.get();
            
            // 사용자 상태 검증
            if (!"ACTIVE".equals(user.getStatus())) {
                System.out.println("세션 무효화: 사용자 상태가 ACTIVE가 아님 - " + user.getStatus());
                session.invalidate();
                return null;
            }
            
            // 삭제된 사용자 검증
            if (Boolean.TRUE.equals(user.getIsDeleted())) {
                System.out.println("세션 무효화: 삭제된 사용자");
                session.invalidate();
                return null;
            }
            
            // 검증 통과 시 DTO 생성
            SocialUserDTO userDto = new SocialUserDTO();
            userDto.setId(user.getUserId().longValue());
            userDto.setEmail(user.getEmail());
            userDto.setName(user.getName());
            userDto.setProvider(user.getProvider());
            return userDto;
            
        } catch (Exception e) {
            System.err.println("getLoginUser 오류: " + e.getMessage());
            session.invalidate();
            return null;
        }
    }
}

