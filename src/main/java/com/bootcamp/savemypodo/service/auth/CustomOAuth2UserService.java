package com.bootcamp.savemypodo.service.auth;

import com.bootcamp.savemypodo.entity.CustomOAuth2User;
import com.bootcamp.savemypodo.entity.User;
import com.bootcamp.savemypodo.global.enums.Provider;
import com.bootcamp.savemypodo.global.enums.Role;
import com.bootcamp.savemypodo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        log.info("🔐 [OAuth2] 사용자 정보 로딩 시작");

        OAuth2User oAuth2User = super.loadUser(request);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        log.info("✅ [OAuth2] 사용자 정보 가져오기 성공: {}", attributes);

        try {
            String email = (String) attributes.get("email");
            String name = (String) attributes.get("name");
            String picture = (String) attributes.get("picture");
            String providerId = (String) attributes.get("sub");

            log.info("📨 [OAuth2] 사용자 이메일: {}, 이름: {}, 프로필: {}", email, name, picture);

            if (email == null || providerId == null) {
                throw new IllegalArgumentException("email 또는 providerId가 null입니다.");
            }

            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        log.info("🆕 [OAuth2] 신규 사용자 - DB에 저장 중: {}", email);
                        return userRepository.save(
                                User.builder()
                                        .email(email)
                                        .nickname(name)
                                        .profileImageUrl(picture)
                                        .provider(Provider.GOOGLE)
                                        .providerId(providerId)
                                        .role(Role.USER)
                                        .build()
                        );
                    });

            log.info("🙆 [OAuth2] 사용자 인증 처리 완료: {}", user.getEmail());

            return new CustomOAuth2User(
                    user.getEmail(),
                    user.getRole(),
                    attributes
            );

        } catch (Exception e) {
            log.error("❌ [OAuth2] 사용자 처리 중 오류 발생", e);
            throw e;
        }
    }
}