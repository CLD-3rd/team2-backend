//package com.bootcamp.savemypodo;
//
//import com.bootcamp.savemypodo.entity.Musical;
//import com.bootcamp.savemypodo.entity.Reservation;
//import com.bootcamp.savemypodo.entity.Seat;
//import com.bootcamp.savemypodo.entity.User;
//import com.bootcamp.savemypodo.repository.MusicalRepository;
//import com.bootcamp.savemypodo.repository.ReservationRepository;
//import com.bootcamp.savemypodo.repository.SeatRepository;
//import com.bootcamp.savemypodo.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.test.annotation.Rollback;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Rollback(false)
//public class MyReservationDummyDataTest {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private MusicalRepository musicalRepository;
//
//    @Autowired
//    private SeatRepository seatRepository;
//
//    @Autowired
//    private ReservationRepository reservationRepository;
//
//    private User user1;
//
//    @BeforeEach
//    void setUp() {
//        // 이미 존재하는 UID=1 유저 조회
//        user1 = userRepository.findById(1L)
//                .orElseThrow(() -> new RuntimeException("User with ID 1 not found."));
//
//        for (int i = 1; i <= 3; i++) {
//            // 뮤지컬 생성
//            Musical musical = Musical.builder()
//                    .title("뮤지컬 " + i)
//                    .posterUrl("/images/musical" + i + ".jpg")
//                    .startTime(LocalTime.of(14 + i, 0))
//                    .endTime(LocalTime.of(16 + i, 30))
//                    .date(LocalDate.of(2025, 8, i))
//                    .description("설명 " + i)
//                    .price(70000L + (i * 5000))
//                    .location("서울극장 " + i)
//                    .duration(120L + (i * 10))
//                    .reservedCount(0L)
//                    .build();
//            musicalRepository.save(musical);
//
//            // 좌석 생성
//            Seat seat = Seat.builder()
//                    .musical(musical)
//                    .row((char) ('A' + i))
//                    .column(i)
//                    .build();
//            seatRepository.save(seat);
//
//            // 예약 생성
//            Reservation reservation = Reservation.builder()
//                    .user(user1)
//                    .musical(musical)
//                    .seat(seat)
//                    .build();
//            reservationRepository.save(reservation);
//        }
//    }
//
//    @Test
//    void 유저1번의_예약_3개_확인() {
//        List<Reservation> reservations = reservationRepository.findAll();
//        assertThat(reservations).hasSize(3);
//        reservations.forEach(res -> {
//            assertThat(res.getUser().getId()).isEqualTo(1L);
//        });
//    }
//}

