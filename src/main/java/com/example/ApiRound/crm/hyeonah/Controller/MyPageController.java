package com.example.ApiRound.crm.hyeonah.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyPageController {
    
    @GetMapping("/mypage")
    public String mypage() {
        return "crm/mypage";
    }
}
