package com.snowman.team2.global.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorDTO {
    private int status;
    private String code;
    private String msg;
    private String detail;

    public static ResponseEntity<ErrorDTO> toResponseEntity(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity.status(errorCode.getStatus().value())
                .body(ErrorDTO.builder()
                        .status(errorCode.getStatus().value())
                        .code(errorCode.getCode())
                        .msg(errorCode.getMsg())
                        .detail(e.getMessage())
                        .build());
    }
}
