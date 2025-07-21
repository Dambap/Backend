package com.double_o.dambap.exception.dto;

import java.io.Serializable;
import lombok.Getter;

@Getter
public class ErrorDto implements Serializable {

    private final String message;
    private final String reason;

    public ErrorDto(ErrorType message) {
        this.message = message.name();
        this.reason = message.getMessage();
    }

}
