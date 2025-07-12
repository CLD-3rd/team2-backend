package com.bootcamp.savemypodo.dto.musical;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class RedisMusicalResponse {  // Redis에 직접 접근해서 값을 바꾸기 위한 클래스
    private Long id;
    private String title;
    private String timeRange;
    private String description;
    private Long remainingSeats;
    private Long totalSeats;
    private Long price;
    private String posterUrl;
    private boolean isReserved;
    private LocalDate date;
    private String location;
    private Long duration;

    /**
     * remainingSeats 와 isReserved 만 바꿔서 새로운 DTO 생성
     */
    public RedisMusicalResponse updateEntry(int deltaRemaining, boolean newReserved) {
        return this.toBuilder()
                   .remainingSeats(this.remainingSeats + deltaRemaining)
                   .isReserved(newReserved)
                   .build();
    }

    /**
     * isReserved 플래그만 새로 설정하여 반환
     */
    public RedisMusicalResponse refreshReserved(boolean newReserved) {
        return this.toBuilder()
                   .isReserved(newReserved)
                   .build();
    }
}
