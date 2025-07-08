package com.bootcamp.savemypodo.entity;

import jakarta.persistence.*;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;               // OAuth2 제공 이메일
    private String name;                // 사용자 이름
    private String profileImageUrl;     // 프로필 사진 URL
    private String provider;            // ex) google, kakao, github
    private String providerId;          // 해당 provider 내 유저 ID

    @Enumerated(EnumType.STRING)
    private Role role;                  // 사용자 역할 (예: USER, ADMIN)

    private LocalDateTime createdAt;    // 가입 시각
    private LocalDateTime updatedAt;    // 마지막 로그인 or 정보 수정 시각
}