package com.double_o.dambap.exception.s3;

import com.double_o.dambap.exception.BusinessException;
import com.double_o.dambap.exception.dto.ErrorType;

public class S3InvalidException extends BusinessException {

    public S3InvalidException(ErrorType errorType) {
        super(errorType);
    }
}
