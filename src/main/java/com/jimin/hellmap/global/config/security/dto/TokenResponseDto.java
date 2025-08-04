package com.jimin.hellmap.global.config.security.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenResponseDto {
    private String accessToken;
}
