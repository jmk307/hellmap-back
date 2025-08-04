package com.jimin.hellmap.domain.member.dto;

import jakarta.annotation.Nonnull;

public record MemberRequestDto(
        @Nonnull
        String provider,

        @Nonnull
        String providerId,

        @Nonnull
        String nickname
) {
}
