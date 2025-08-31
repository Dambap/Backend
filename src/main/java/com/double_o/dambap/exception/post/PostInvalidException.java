package com.double_o.dambap.exception.post;

import com.double_o.dambap.exception.BusinessException;
import com.double_o.dambap.exception.dto.ErrorType;

public class PostInvalidException extends BusinessException {

    public PostInvalidException(ErrorType errorType) {
        super(errorType);
    }
}
