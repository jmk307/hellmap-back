package com.jimin.hellmap.global.config.oauth;

import com.jimin.hellmap.domain.member.MemberRepository;
import com.jimin.hellmap.global.config.oauth.dto.GoogleMemberInfoDto;
import com.jimin.hellmap.global.config.oauth.dto.KakaoMemberDto;
import com.jimin.hellmap.global.config.oauth.dto.OauthAccessToken;
import com.jimin.hellmap.global.error.ErrorCode;
import com.jimin.hellmap.global.error.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class KakaoUtils {
    private final WebClient webClient;
    private final MemberRepository memberRepository;

    // 카카오 정보 가져오기
    @Transactional(readOnly = true)
    public KakaoMemberDto getKakaoMemberInfo(OauthAccessToken oauthAccessToken) {
        final String kakaoMemberInfoUrl = "https://kapi.kakao.com/v2/user/me";

        try {
            return webClient.get()
                    .uri(kakaoMemberInfoUrl)
                    .header("Authorization", "Bearer " + oauthAccessToken.getAccessToken())
                    .retrieve()
                    .bodyToMono(KakaoMemberDto.class)
                    .block();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new BadRequestException(ErrorCode.GOOGLE_BAD_REQUEST);
        }
    }
}
