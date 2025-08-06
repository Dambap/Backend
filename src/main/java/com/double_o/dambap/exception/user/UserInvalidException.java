package com.double_o.dambap.exception.user;

import com.double_o.dambap.exception.BusinessException;
import com.double_o.dambap.exception.dto.ErrorType;

public class UserInvalidException extends BusinessException {

    public UserInvalidException(ErrorType errorType) {
        super(errorType);
    }
}
