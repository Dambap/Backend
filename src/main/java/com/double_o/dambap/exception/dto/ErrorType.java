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
    USER_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."),

    //auth 예외
    NON_IDENTICAL_USER_ERROR(HttpStatus.FORBIDDEN, "작성자와 접근자가 일치하지 않습니다."),
    WRITER_CANNOT_RECOMMEND_ERROR(HttpStatus.FORBIDDEN, "자신의 게시물은 추천할 수 없습니다."),
    USED_ACCESS_TOKEN_ERROR(HttpStatus.FORBIDDEN, "이미 사용된 엑세스 토큰입니다"),
    USED_REFRESH_TOKEN_ERROR(HttpStatus.FORBIDDEN, "이미 사용된 리프레시 토큰입니다"),
    REFRESH_TOKEN_MISMATCH_ERROR(HttpStatus.FORBIDDEN, "엑세스 토큰이 일치하지 않습니다"),
    INVALID_ACCESS_TOKEN_ERROR(HttpStatus.FORBIDDEN, "잘못된 액세스 토큰입니다"),
    INVALID_REFRESH_TOKEN_ERROR(HttpStatus.FORBIDDEN, "잘못된 리프레시 토큰입니다"),
    EXPIRED_REFRESH_TOKEN_ERROR(HttpStatus.FORBIDDEN, "만료된 리프레시 토큰입니다"),
    WRONG_PASSWORD_ERROR(HttpStatus.UNAUTHORIZED, "잘못된 비밀번호입니다"),

    // 로그인 예외
    FAIL_TO_LOGIN_ERROR(HttpStatus.UNAUTHORIZED, "로그인에 실패했습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 틀렸습니다.");

    private final HttpStatus status;
    private final String message;
}
