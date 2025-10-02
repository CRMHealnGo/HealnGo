package com.example.ApiRound.Service;

import com.example.ApiRound.entity.SocialUser;

public interface KakaoSocialUserService {
    SocialUser findOrCreateUser(String email, String name, String providerId, String profileImage, String accessToken, String refreshToken);
    SocialUser findByEmailAndProvider(String email, String provider);
}
