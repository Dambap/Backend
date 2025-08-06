package com.double_o.dambap.user.application;

import com.double_o.dambap.exception.dto.ErrorType;
import com.double_o.dambap.exception.user.UserInvalidException;
import com.double_o.dambap.exception.user.UserRegisterInvalidException;
import com.double_o.dambap.user.domain.User;
import com.double_o.dambap.user.dto.request.UserRegisterRequest;
import com.double_o.dambap.user.dto.response.UserRegisterResponse;
import com.double_o.dambap.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    @Override
    public UserRegisterResponse registerUser(UserRegisterRequest request) {
        // 이메일 중복 검증
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserRegisterInvalidException(ErrorType.EMAIL_DUPLICATE_ERROR);
        }

        if (validatePassword(request.getPassword())) {
            throw new UserRegisterInvalidException(ErrorType.PASSWORD_PATTERN_ERROR);
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new UserRegisterInvalidException(ErrorType.CONFIRM_PASSWORD_NOT_MATCH_ERROR);
        }

        User savedUser = buildAndSaveUser(request);

        return UserRegisterResponse.toResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getPassword(),
                savedUser.getName(),
                savedUser.getBirthday());
    }

    private User buildAndSaveUser(UserRegisterRequest request) {
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .birthday(request.getBirthday())
                .build();
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    @Override
    public User getByEmail(String email) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UserInvalidException(ErrorType.USER_NOT_FOUND_ERROR));
    }

    @Override
    public boolean validatePassword(String password) {
        return password.matches("^(?=.*[a-z])(?=.*\\d)[a-z\\d]{8,}$");
    }

}
