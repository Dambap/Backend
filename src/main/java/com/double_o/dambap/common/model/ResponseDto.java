package com.double_o.dambap.common.model;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseDto<T> implements Serializable {

    private T data;
    private String errorMessage;

    public ResponseDto(T data) {
        this.data = data;
    }

    public static <T> ResponseEntity<ResponseDto<T>> ok(T data) {
        return ResponseEntity.ok(new ResponseDto<T>(data));
    }

    public static <T> ResponseEntity<ResponseDto<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto<T>(data));
    }

    public static <T> ResponseEntity<ResponseDto<T>> notFound() {
        ResponseDto<T> responseDto = new ResponseDto<>();
        responseDto.setErrorMessage("The requested resource does not exist.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDto);
    }

    public static ResponseEntity<Void> noContent() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
