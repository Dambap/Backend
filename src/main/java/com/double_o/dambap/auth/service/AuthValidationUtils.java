package com.double_o.dambap.auth.service;


import com.double_o.dambap.exception.auth.AuthInvalidException;
import com.double_o.dambap.exception.dto.ErrorType;

public class AuthValidationUtils {

    public static void verifySameUser(Long comparedUserId, Long originalUserId) {
        if (!comparedUserId.equals(originalUserId)) {
            throw new AuthInvalidException(ErrorType.NON_IDENTICAL_USER_ERROR);
        }
    }
}
