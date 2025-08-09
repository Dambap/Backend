package com.double_o.dambap.exception.user;

import com.double_o.dambap.exception.BusinessException;
import com.double_o.dambap.exception.dto.ErrorType;

public class UserRegisterInvalidException extends BusinessException {

    public UserRegisterInvalidException(ErrorType errorType) {
        super(errorType);
    }
}
