package com.double_o.dambap.user.application;

import com.double_o.dambap.exception.dto.ErrorType;
import com.double_o.dambap.exception.user.UserInvalidException;
import com.double_o.dambap.user.domain.User;
import com.double_o.dambap.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserValidationService {

    private final UserRepository userRepository;

    // 유저 반환, 없으면 예외처리
    public User getUserOrThrowIfNotExist(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("[ValidationError] User not found. userId={}", userId);
            return new UserInvalidException(ErrorType.USER_NOT_FOUND_ERROR);
        });
    }
}
