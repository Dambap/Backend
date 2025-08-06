package com.double_o.dambap.exception.auth;


import com.double_o.dambap.exception.BusinessException;
import com.double_o.dambap.exception.dto.ErrorType;

public class AuthInvalidException extends BusinessException {

    public AuthInvalidException(ErrorType errorType) {
        super(errorType);
    }
}
