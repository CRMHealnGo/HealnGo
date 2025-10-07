package com.example.ApiRound.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "spring.security.disabled", havingValue = "true", matchIfMissing = false)
public class SecurityConfig {
    // Spring Security 비활성화를 위한 설정 클래스
    // 실제로는 application.properties에서 spring.security.disabled=true로 설정
}
