package com.jimin.hellmap.global.config.oauth;

import com.jimin.hellmap.domain.member.MemberRepository;
import com.jimin.hellmap.global.config.oauth.dto.OauthAccessToken;
import com.jimin.hellmap.global.config.oauth.dto.GoogleMemberInfoDto;
import com.jimin.hellmap.global.error.ErrorCode;
import com.jimin.hellmap.global.error.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Service
public class GoogleUtils {
    private final WebClient webClient;
    private final MemberRepository memberRepository;

    // 구글 정보 가져오기
    @Transactional(readOnly = true)
    public GoogleMemberInfoDto getGoogleMemberInfo(OauthAccessToken oauthAccessToken) {
        final String googleMemberInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";

        try {
            return webClient.get()
                    .uri(googleMemberInfoUrl)
                    .header("Authorization", "Bearer " + oauthAccessToken.getAccessToken())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(GoogleMemberInfoDto.class)
                    .block();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new BadRequestException(ErrorCode.GOOGLE_BAD_REQUEST);
        }
    }
}
