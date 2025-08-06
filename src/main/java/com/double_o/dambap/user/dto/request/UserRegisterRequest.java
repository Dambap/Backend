package com.double_o.dambap.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Data;

@Data   // getter +  setter
public class UserRegisterRequest {

    @NotNull(message = "이메일은 필수입니다.")
    private String email;

    @NotNull(message = "비밀번호는 필수입니다.")
    private String password;

    @JsonProperty("confirmPassword")
    private String confirmPassword;

    @NotNull(message = "사용자 이름은 필수입니다.")
    @Size(max = 5, message = "사용자의 이름은 최대 5글자 입니다.")
    private String name;

    @NotNull(message = "생년월일은 필수입니다.")
    private LocalDate birthday;
}
