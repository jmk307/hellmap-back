package com.jimin.hellmap.global.config.security.jwt.auth;

import com.jimin.hellmap.domain.member.MemberRepository;
import com.jimin.hellmap.global.config.security.jwt.annotation.LoginUser;
import com.jimin.hellmap.global.error.ErrorCode;
import com.jimin.hellmap.global.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class MemberHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    private final MemberRepository memberRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(LoginUser.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            return memberRepository.findByProviderId(authentication.getName())
                    .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        }

        return null;
    }
}
