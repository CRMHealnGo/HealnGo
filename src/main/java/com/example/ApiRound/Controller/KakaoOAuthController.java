package com.example.ApiRound.Controller;

import com.example.ApiRound.Service.SocialUserService;
import com.example.ApiRound.dto.SocialUserDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

// @Controller  // 임시로 비활성화
@RequestMapping("/oauth/kakao")
public class KakaoOAuthController {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Autowired
    @Qualifier("kakaoSocialUserServiceImpl")
    private SocialUserService socialUserService;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/login-url")
    public String getKakaoLoginUrl() {
        return "https://kauth.kakao.com/oauth/authorize"
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=code";
    }

    @GetMapping("/callback")
    public RedirectView kakaoCallback(@RequestParam String code, HttpSession session) {
        // SocialUserDTO user = socialUserService.processKakaoLogin(code);  // 임시로 주석 처리
        SocialUserDTO user = null;  // 임시로 null 설정
        session.setAttribute("loginUser", user);
        // session.setAttribute("accessToken", user.getAccessToken());  // 임시로 주석 처리
        return new RedirectView("/main");
    }

}
