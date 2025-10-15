package com.example.ApiRound.crm.hyeonah.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class MyPageController {
    
    @GetMapping("/mypage")
    public String mypage(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        
        if (userId == null) {
            return "redirect:/login";
        }
        
        // 사용자 정보를 모델에 추가
        model.addAttribute("userId", userId);
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userEmail", session.getAttribute("userEmail"));
        
        return "crm/mypage";
    }
}
