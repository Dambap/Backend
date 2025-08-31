package com.double_o.dambap.auth.service;


import com.double_o.dambap.exception.auth.AuthInvalidException;
import com.double_o.dambap.exception.dto.ErrorType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthValidationUtils {

    public static void verifySameUser(Long targetUserId, Long loginUserId) {
        if (!targetUserId.equals(loginUserId)) {
            log.warn("[AuthViolation] verifySameUser failed: targetUserId={}, loginUserId={}",
                    targetUserId, loginUserId);
            throw new AuthInvalidException(ErrorType.NON_IDENTICAL_USER_ERROR);
        }
    }
}
