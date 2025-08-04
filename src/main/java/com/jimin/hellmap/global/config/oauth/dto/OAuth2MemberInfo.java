package com.jimin.hellmap.global.config.oauth.dto;

import com.jimin.hellmap.domain.member.entity.Member;
import com.jimin.hellmap.domain.member.model.Social;
import com.jimin.hellmap.global.error.ErrorCode;
import com.jimin.hellmap.global.error.exception.BadRequestException;
import jakarta.security.auth.message.AuthException;
import lombok.Builder;

import java.util.Map;

@Builder
public record OAuth2MemberInfo(
        String name,
        String email,
        String providerId
) {

    public static OAuth2MemberInfo of(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) { // registration id별로 userInfo 생성
            case "google" -> ofGoogle(attributes);
            case "kakao" -> ofKakao(attributes);
            default -> throw new BadRequestException(ErrorCode.SOCIAL_ALREADY_EXIST);
        };
    }

    private static OAuth2MemberInfo ofGoogle(Map<String, Object> attributes) {
        return OAuth2MemberInfo.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .providerId((String) attributes.get("sub"))
                .build();
    }

    private static OAuth2MemberInfo ofKakao(Map<String, Object> attributes) {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");

        return OAuth2MemberInfo.builder()
                .name((String) profile.get("nickname"))
                .email((String) account.get("email"))
                .providerId((String) attributes.get("sub"))
                .build();
    }

    public Member toEntity(OAuth2MemberInfo oAuth2MemberInfo, String providerId) {
        return Member.builder()
                .nickname(providerId)
                .provider(Social.from(providerId))
                .providerId(oAuth2MemberInfo.providerId)
                .build();
    }
}
