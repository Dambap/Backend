package com.double_o.dambap.user.presentation;

import com.double_o.dambap.common.model.ResponseDto;
import com.double_o.dambap.user.application.UserService;
import com.double_o.dambap.user.dto.request.UserRegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원가입")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @Parameter(required = true, description = "회원 가입 요청") @RequestBody UserRegisterRequest request) {
        var response = userService.registerUser(request);
        return ResponseDto.created(response);
    }
}
