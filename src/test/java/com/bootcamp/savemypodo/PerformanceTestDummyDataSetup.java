package com.bootcamp.savemypodo;

import com.bootcamp.savemypodo.entity.Musical;
import com.bootcamp.savemypodo.entity.Seat;
import com.bootcamp.savemypodo.repository.MusicalRepository;
import com.bootcamp.savemypodo.repository.SeatRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ActiveProfiles("performance")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Rollback(false)
@RequiredArgsConstructor
@Transactional
public class PerformanceTestDummyDataSetup {

    @Autowired
    private MusicalRepository musicalRepository;

    @Autowired
    private SeatRepository seatRepository;

    @BeforeAll
    void createDummyMusicals() {
        if (musicalRepository.count() >= 6) return;

        for (int i = 1; i <= 200; i++) {
            Musical musical = Musical.builder()
                    .title("테스트 공연 " + i)
                    .posterUrl("https://dummy.image/poster" + i + ".jpg")
                    .startTime(LocalTime.of(18, 0))
                    .endTime(LocalTime.of(20, 30))
                    .description("테스트용 공연 설명 " + i)
                    .date(LocalDate.now().plusDays(i))
                    .price(40000L + i * 1000)
                    .location("테스트 공연장 " + i)
                    .duration(150L)
                    .reservedCount((long) (Math.random() * 100))  // 예매수 랜덤
                    .build();

            musicalRepository.save(musical);
        }
    }

    @Test
    void createDummySeatsForMusical1() {
        Musical musical = musicalRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Musical with ID 1 not found"));

        List<Seat> seats = new ArrayList<>();
        int numRows = 10;  // A ~ J
        int numCols = 14;  // 1 ~ 14

        for (int row = 0; row < numRows; row++) {
            char rowChar = (char) ('A' + row);
            for (int col = 1; col <= numCols; col++) {
                String seatName = String.valueOf(rowChar) + col;
                Seat seat = Seat.builder()
                        .musical(musical)
                        .seatName(seatName)
                        .build();
                seats.add(seat);
            }
        }

        seatRepository.saveAll(seats);
        System.out.println("✅ 총 생성된 좌석 수: " + seats.size());
    }

}