package com.bootcamp.savemypodo.global.exception;

public class MusicalException extends RuntimeException {

    private final ErrorCode errorCode;

    public MusicalException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
