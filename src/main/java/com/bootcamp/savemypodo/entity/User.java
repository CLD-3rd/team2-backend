package com.bootcamp.savemypodo.entity;

import com.bootcamp.savemypodo.global.enums.Provider;
import com.bootcamp.savemypodo.global.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 사용자 고유 ID (PK)

    @Column(nullable = false, length = 255, unique = true)
    private String email; // 소셜 로그인 이메일

    @Column(length = 255)
    private String nickname; // 사용자 닉네임

    @Column(name = "profile_image_url", length = 512)
    private String profileImageUrl; // 소셜 프로필 사진 URL

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider; // 소셜 로그인 제공자 (예: GOOGLE)

    @Column(name = "provider_id", length = 255)
    private String providerId; // 공급자 내 고유 사용자 ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // 사용자 권한 (예: USER, ADMIN)

    // 유저 권한 설정 메소드
    public void authorizeUser() {
        this.role = Role.USER;
    }
}




 
