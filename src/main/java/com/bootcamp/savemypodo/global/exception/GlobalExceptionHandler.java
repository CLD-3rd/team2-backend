package com.bootcamp.savemypodo.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("⚠️ IllegalArgumentException: {}", e.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException e, HttpServletRequest request) {
        log.error("❌ RuntimeException: {}", e.getMessage());
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAll(Exception e, HttpServletRequest request) {
        log.error("🔥 Unhandled Exception: {}", e.getMessage(), e);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", request.getRequestURI());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri.startsWith("/.well-known")) {
            log.debug("📭 무시된 정적 자원 요청: {}", uri);  // 로그 레벨은 debug로
            return ResponseEntity.notFound().build(); // 404 quietly
        }
        // 혹시 다른 경로에서 발생했다면 로그
        log.error("❌ NoResourceFoundException 처리되지 않음: {}", uri);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<?> handleUserException(UserException e, HttpServletRequest request) {
        ErrorCode code = e.getErrorCode();
        log.warn("🚫 UserException: {} - {}", code.getStatus(), code.getMessage());
        return buildResponse(code.getStatus(), code.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MusicalException.class)
    public ResponseEntity<?> handleUserException(MusicalException e, HttpServletRequest request) {
        ErrorCode code = e.getErrorCode();
        log.warn("🎤 MusicalException: {} - {}", code.getStatus(), code.getMessage());
        return buildResponse(code.getStatus(), code.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ReservationException.class)
    public ResponseEntity<?> handleUserException(ReservationException e, HttpServletRequest request) {
        ErrorCode code = e.getErrorCode();
        log.warn("📅 ReservationException: {} - {}", code.getStatus(), code.getMessage());
        return buildResponse(code.getStatus(), code.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(SeatException.class)
    public ResponseEntity<?> handleUserException(SeatException e, HttpServletRequest request) {
        ErrorCode code = e.getErrorCode();
        log.warn("🪑 SeatException: {} - {}", code.getStatus(), code.getMessage());
        return buildResponse(code.getStatus(), code.getMessage(), request.getRequestURI());
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message, String path) {
        return ResponseEntity
                .status(status)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", status.value(),
                        "error", status.getReasonPhrase(),
                        "message", message,
                        "path", path
                ));
    }
}