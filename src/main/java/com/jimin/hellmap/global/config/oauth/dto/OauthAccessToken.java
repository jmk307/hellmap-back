package com.jimin.hellmap.global.config.oauth.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OauthAccessToken {
    private String accessToken;

    @Builder
    public OauthAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
