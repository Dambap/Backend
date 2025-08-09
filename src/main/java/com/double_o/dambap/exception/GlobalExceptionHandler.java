package com.double_o.dambap.exception;

import com.double_o.dambap.common.dto.FieldInvalidResponse;
import com.double_o.dambap.exception.dto.ErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 사용자 정의 예외 처리
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorDto> handleBusinessException(final BusinessException e) {
        log.error("[Error] BusinessException -> {}", e.getMessage());
        return ResponseEntity.status(e.getErrorType().getStatus())
                .body(new ErrorDto(e.getErrorType()));
    }

    // 메소드 파라미터의 not valid 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<FieldInvalidResponse> handleFieldException(
            final MethodArgumentNotValidException e
    ) {
        BindingResult bindingResult = e.getBindingResult();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(FieldInvalidResponse.builder()
                        .errorCode(bindingResult.getFieldError().getCode())
                        .errorMessage(bindingResult.getFieldError().getDefaultMessage())
                        .build());
    }
}
