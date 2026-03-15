package com.snowman.team2.global.exception.exceptionType;

import com.snowman.team2.global.exception.CustomException;
import com.snowman.team2.global.exception.ErrorCode;

public class UnauthorizedException extends CustomException {
    public UnauthorizedException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
