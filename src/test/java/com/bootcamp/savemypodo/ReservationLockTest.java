//package com.bootcamp.savemypodo;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.Collections;
//
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.Rollback;
//
//import com.bootcamp.savemypodo.entity.Musical;
//import com.bootcamp.savemypodo.entity.User;
//import com.bootcamp.savemypodo.global.enums.Provider;
//import com.bootcamp.savemypodo.global.enums.Role;
//import com.bootcamp.savemypodo.repository.MusicalRepository;
//import com.bootcamp.savemypodo.repository.UserRepository;
//import com.bootcamp.savemypodo.service.ReservationService;
//
//
//@SpringBootTest
//@DisplayName("동시 좌석 예약 테스트")
//@Rollback
//class ReservationLockTest {
//
//    @Autowired
//    private ReservationService reservationService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private MusicalRepository musicalRepository;
//
//    private User user1;
//    private User user2;
//    private Musical musical;
//
//    @BeforeEach
//    void setUp() {
//        user1 = userRepository.findByEmail("user1@test.com")
//                .orElseGet(() -> userRepository.save(User.builder()
//                        .email("user1@test.com")
//                        .nickname("user1")
//                        .provider(Provider.GOOGLE)
//                        .providerId("u1")
//                        .role(Role.USER)
//                        .build()));
//
//        user2 = userRepository.findByEmail("user2@test.com")
//                .orElseGet(() -> userRepository.save(User.builder()
//                        .email("user2@test.com")
//                        .nickname("user2")
//                        .provider(Provider.GOOGLE)
//                        .providerId("u2")
//                        .role(Role.USER)
//                        .build()));
//
//        musical = musicalRepository.save(Musical.builder()
//                .title("동시성 테스트 뮤지컬")
//                .posterUrl("https://example.com/poster.jpg")
//                .startTime(LocalTime.of(19, 0))
//                .endTime(LocalTime.of(21, 0))
//                .description("락 테스트용 공연")
//                .date(LocalDate.now().plusDays(1))
//                .price(10000L)
//                .location("테스트 공연장")
//                .reservedCount(0L)
//                .build());
//    }
//
//
//    @Test
//    @DisplayName("동일 좌석을 동시에 예약하면 하나만 성공해야 함")
//    void testConcurrentReservation() throws InterruptedException {
//        int threadCount = 2;
//        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());
//
//        String seatName = "A1";
//        Long mid = musical.getId(); // 🎯 한 번만 안전하게 저장해서 공유
//
//        executor.submit(() -> {
//            try {
//                reservationService.createReservationWithLock(user1, mid, seatName);
//            } catch (Exception e) {
//                exceptions.add(e);
//            } finally {
//                latch.countDown();
//            }
//        });
//
//        executor.submit(() -> {
//            try {
//                reservationService.createReservationWithLock(user2, mid, seatName);
//            } catch (Exception e) {
//                exceptions.add(e);
//            } finally {
//                latch.countDown();
//            }
//        });
//
//        latch.await();
//
//        // 예외가 정확히 1개 발생해야 동시성 제어 성공
//        Assertions.assertEquals(1, exceptions.size(), "동시에 예약하면 하나는 실패해야 한다.");
//
//        System.out.println("❗예외 메시지: " + exceptions.get(0).getMessage());
//    }
//
//}
