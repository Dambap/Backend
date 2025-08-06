package com.double_o.dambap.auth.model;

import static com.double_o.dambap.auth.AuthConstants.ACCESS_TOKEN_HEADER_KEY;

import lombok.Data;

@Data
public class AuthToken {

    private final String key;
    private final String token;

    public AuthToken(String token) {
        this.key = ACCESS_TOKEN_HEADER_KEY;
        this.token = token;
    }
}