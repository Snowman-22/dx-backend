package com.snowman.team2.global.exception.exceptionType;

import com.snowman.team2.global.exception.CustomException;
import com.snowman.team2.global.exception.ErrorCode;

public class BadRequestException extends CustomException {
    public BadRequestException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}