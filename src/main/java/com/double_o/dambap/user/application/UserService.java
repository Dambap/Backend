package com.double_o.dambap.user.application;

import com.double_o.dambap.user.dto.request.UserRegisterRequest;
import com.double_o.dambap.user.dto.response.UserRegisterResponse;
import com.double_o.dambap.user.domain.User;

public interface UserService {

    UserRegisterResponse registerUser(UserRegisterRequest request);

    User getByEmail(String email);

    boolean validatePassword(String password);


}