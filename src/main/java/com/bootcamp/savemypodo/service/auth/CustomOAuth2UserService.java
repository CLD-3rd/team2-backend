package com.bootcamp.savemypodo.service.auth;

import com.bootcamp.savemypodo.entity.User;
import com.bootcamp.savemypodo.entity.Role;
import com.bootcamp.savemypodo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId(); // ex) google, github
        String providerId = oAuth2User.getName(); // OAuth2에서 제공하는 사용자 ID
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.getOrDefault("name", "unknown");
        String profileImage = (String) attributes.getOrDefault("picture", "");

        // 기존 사용자 존재 여부 확인
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    // 새로운 사용자 저장
                    return userRepository.save(
                            User.createOAuthUser(email, name, profileImage, provider, providerId)
                    );
                });

        return oAuth2User; // 필요시 커스텀 OAuth2User 반환 가능
    }
}