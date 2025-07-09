package com.bootcamp.savemypodo.service.auth;

import com.bootcamp.savemypodo.entity.Provider;
import com.bootcamp.savemypodo.entity.User;
import com.bootcamp.savemypodo.entity.Role;
import com.bootcamp.savemypodo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User oAuth2User = super.loadUser(request);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");
        String providerId = (String) attributes.get("sub");

        // 사용자 저장 또는 조회
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email(email)
                                .nickname(name)
                                .profileImageUrl(picture)
                                .provider(Provider.GOOGLE)
                                .providerId(providerId)
                                .role(Role.USER)
                                .build()
                ));

        return new CustomOAuth2User(
                user.getEmail(),
                user.getRole(),
                attributes
        );
    }
}