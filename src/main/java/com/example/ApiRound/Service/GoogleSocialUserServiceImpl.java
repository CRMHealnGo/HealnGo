package com.example.ApiRound.Service;

import com.example.ApiRound.entity.SocialUser;
import com.example.ApiRound.repository.SocialUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GoogleSocialUserServiceImpl implements GoogleSocialUserService {
    
    @Autowired
    private SocialUserRepository socialUserRepository;
    
    @Override
    public SocialUser findOrCreateUser(String email, String name, String providerId, String profileImage, String accessToken, String refreshToken) {
        SocialUser existingUser = socialUserRepository.findByEmailAndProvider(email, "google");
        
        if (existingUser != null) {
            // 기존 사용자 정보 업데이트
            existingUser.setName(name);
            existingUser.setProfileImage(profileImage);
            existingUser.setAccessToken(accessToken);
            existingUser.setRefreshToken(refreshToken);
            return socialUserRepository.save(existingUser);
        } else {
            // 새 사용자 생성
            SocialUser newUser = new SocialUser();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setProvider("google");
            newUser.setProviderId(providerId);
            newUser.setProfileImage(profileImage);
            newUser.setAccessToken(accessToken);
            newUser.setRefreshToken(refreshToken);
            return socialUserRepository.save(newUser);
        }
    }
    
    @Override
    public SocialUser findByEmailAndProvider(String email, String provider) {
        return socialUserRepository.findByEmailAndProvider(email, provider);
    }
}