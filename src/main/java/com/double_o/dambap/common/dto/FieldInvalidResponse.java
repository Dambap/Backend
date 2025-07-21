package com.double_o.dambap.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FieldInvalidResponse {

    private String errorCode;
    private String errorMessage;
}
