package com.double_o.dambap.config.resolver;

import com.double_o.dambap.auth.model.AuthUser;
import com.double_o.dambap.exception.auth.AuthInvalidException;
import com.double_o.dambap.exception.dto.ErrorType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // AuthUser 타입의 매개변수를 지원하는지 확인
        return parameter.getParameterType().equals(AuthUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        // JwtFilter 에서 설정한 authUser 가져오기
        AuthUser authUser = (AuthUser) request.getAttribute("authUser");

        // authUser 가 null 일 경우 예외 발생
        if (authUser == null) {
            if (parameter.isOptional()) {
                return null;
            }
            throw new AuthInvalidException(ErrorType.USER_NOT_FOUND_ERROR);
        }
        return authUser;
    }
}