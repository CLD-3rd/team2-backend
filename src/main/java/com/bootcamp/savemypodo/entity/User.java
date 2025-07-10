package com.bootcamp.savemypodo.entity;

import com.bootcamp.savemypodo.global.enums.Provider;
import com.bootcamp.savemypodo.global.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "USERS")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;               // OAuth2 제공 이메일
    private String nickname;                // 사용자 이름
    private String profileImageUrl;     // 프로필 사진 URL

    @Enumerated(EnumType.STRING)
    private Provider provider;        // ex) google, kakao, github
    private String providerId;          // 해당 provider 내 유저 ID

    @Enumerated(EnumType.STRING)
    private Role role;                  // 사용자 역할 (예: USER, ADMIN)

    private String refreshToken; // 리프레시 토큰

    // 유저 권한 설정 메소드
    public void authorizeUser() {
        this.role = Role.USER;
    }

    public void updateRefreshToken(String updateRefreshToken) {
        this.refreshToken = updateRefreshToken;
    }
}