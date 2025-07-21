package com.double_o.dambap.exception;

import com.double_o.dambap.exception.dto.ErrorType;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private ErrorType errorType;
    private String customMessage;

    public BusinessException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    public BusinessException(ErrorType errorType, String customMessage) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.customMessage = customMessage;
    }
}
