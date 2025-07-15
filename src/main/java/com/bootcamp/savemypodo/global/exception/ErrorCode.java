package com.bootcamp.savemypodo.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 유저 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "권한이 없습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),

    // 🔐 토큰 관련 에러
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Access Token이 만료되었습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "Refresh Token이 존재하지 않습니다."),
    REFRESH_TOKEN_MISMATCH(HttpStatus.UNAUTHORIZED, "Refresh Token이 일치하지 않습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Refresh Token이 만료되었습니다."),

    // 🎤 뮤지컬 관련 에러
    MUSICAL_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 뮤지컬입니다."),

    // 🪑 좌석 관련 에러
    INVALID_SEAT_ROW(HttpStatus.BAD_REQUEST, "좌석 행 정보가 올바르지 않습니다. (A~J)"),
    INVALID_SEAT_COLUMN(HttpStatus.BAD_REQUEST, "좌석 열 정보가 유효하지 않습니다. (1~14)"),
    SEAT_ALREADY_RESERVED(HttpStatus.CONFLICT, "해당 좌석은 이미 예약되었습니다."),
	ALREADY_RESERVED_MUSICAL(HttpStatus.CONFLICT, "해당 뮤지컬을 이미 예약하였습니다."),
	SEAT_LOCK_FAILED(HttpStatus.CONFLICT,"좌석 예약 중 다른 사용자가 먼저 시도했습니다.");

	

    // 📅 예약 관련 에러

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}