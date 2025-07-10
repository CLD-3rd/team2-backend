package com.bootcamp.savemypodo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "musicals")
public class Musical {

    public static final int TOTAL_SEATS = 140;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 공연 고유 ID (PK, AI)

    @Column(nullable = false, length = 100)
    private String title; // 공연 제목

    @Column(name = "poster_url", columnDefinition = "TEXT")
    private String posterUrl; // 포스터 이미지 URL

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime; // 공연 시작 시간

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime; // 공연 종료 시간

    @Column(length = 255)
    private String description; // 공연 설명

    @Column(nullable = false)
    private LocalDate date; // 공연 날짜

    @Column(nullable = false)
    private Long price; // 공연 가격

    @Column(nullable = false, length = 50)
    private String location; // 공연장 이름

    private Long duration; // 총 소요 시간 (nullable)

    @Column(name = "reserved_count", nullable = false)
    private Long reservedCount = 0L; // 예매된 좌석 수 (캐시용)
}

