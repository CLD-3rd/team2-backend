package com.bootcamp.savemypodo.global.exception;

public class SeatException extends RuntimeException {

    private final ErrorCode errorCode;

    public SeatException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
