package com.double_o.dambap.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRegisterResponse {

    @JsonProperty("userId")
    private Long id;
    private String email;
    private String password;
    private String name;
    private LocalDate birthday;

    public static UserRegisterResponse toResponse(Long userId, String email, String password,
            String name, LocalDate birthday) {
        return new UserRegisterResponse(userId, email, password, name, birthday);
    }
}
