package com.double_o.dambap.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthLoginResponse {

    private String accessToken;
    private String refreshToken;
}
