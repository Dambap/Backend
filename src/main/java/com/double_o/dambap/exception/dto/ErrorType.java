package com.double_o.dambap.exception.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorType {
    CONFLICT_ERROR(HttpStatus.BAD_REQUEST, "예기치 못한 에러가 발생했습니다."),

    // register 예외
    EMAIL_DUPLICATE_ERROR(HttpStatus.BAD_REQUEST, "중복된 이메일입니다."),
    CONFIRM_PASSWORD_NOT_MATCH_ERROR(HttpStatus.BAD_REQUEST, "패스워드가 확인 패스워드랑 일치하지 않습니다."),
    PASSWORD_PATTERN_ERROR(HttpStatus.BAD_REQUEST, "패스워드에 적절하지 않은 기호를 포함하고 있습니다."),

    // 사용자 예외
    USER_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
