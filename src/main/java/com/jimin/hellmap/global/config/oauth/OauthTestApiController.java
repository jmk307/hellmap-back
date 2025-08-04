package com.jimin.hellmap.global.config.oauth;

import com.jimin.hellmap.global.config.CommonApiResponse;
import com.jimin.hellmap.global.config.oauth.dto.KakaoMemberDto;
import com.jimin.hellmap.global.config.oauth.dto.OauthAccessToken;
import com.jimin.hellmap.global.config.oauth.dto.GoogleMemberInfoDto;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "OAUTH API 테스트")
@RequestMapping("api")
@Hidden
public class OauthTestApiController {
    private final GoogleUtils googleUtils;
    private final KakaoUtils kakaoUtils;

    @PostMapping("google")
    @Operation(summary = "Google 정보 가져오기")
    public ResponseEntity<CommonApiResponse<GoogleMemberInfoDto>> getGoogleInfo(
            @RequestBody OauthAccessToken oauthAccessToken) {
        return ResponseEntity.ok(CommonApiResponse.of(googleUtils.getGoogleMemberInfo(oauthAccessToken)));
    }

    @PostMapping("kakao")
    @Operation(summary = "Kakao 정보 가져오기")
    public ResponseEntity<CommonApiResponse<KakaoMemberDto>> getKakaoInfo(
            @RequestBody OauthAccessToken oauthAccessToken) {
        return ResponseEntity.ok(CommonApiResponse.of(kakaoUtils.getKakaoMemberInfo(oauthAccessToken)));
    }
}
