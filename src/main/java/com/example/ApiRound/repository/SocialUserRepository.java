package com.example.ApiRound.repository;

import com.example.ApiRound.entity.SocialUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialUserRepository extends JpaRepository<SocialUser, Long> {
    
    // Provider와 ProviderId로 사용자 조회
    Optional<SocialUser> findByProviderAndProviderId(String provider, String providerId);
    
    // 이메일로 사용자 조회
    Optional<SocialUser> findByEmail(String email);
    
    // 이름으로 사용자 조회
    Optional<SocialUser> findByName(String name);
    
    // 이메일과 프로바이더로 사용자 조회
    SocialUser findByEmailAndProvider(String email, String provider);
}
