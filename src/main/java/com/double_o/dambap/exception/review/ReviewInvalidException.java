package com.double_o.dambap.exception.review;

import com.double_o.dambap.exception.BusinessException;
import com.double_o.dambap.exception.dto.ErrorType;

public class ReviewInvalidException extends BusinessException {

    public ReviewInvalidException(ErrorType errorType) {
        super(errorType);
    }
}
