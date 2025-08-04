package com.jimin.hellmap.domain.member.dto;

import lombok.Builder;

@Builder
public record MemberResponseDto(
        String nickname,
        String accessToken
) {
    public static MemberResponseDto of(String nickname, String accessToken) {
        return MemberResponseDto.builder()
                .nickname(nickname)
                .accessToken(accessToken)
                .build();
    }
}
