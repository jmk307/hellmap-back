package com.jimin.hellmap.domain.member;

import com.jimin.hellmap.domain.member.dto.MemberRequestDto;
import com.jimin.hellmap.domain.member.dto.MemberResponseDto;
import com.jimin.hellmap.domain.member.dto.ValidateMemberDto;
import com.jimin.hellmap.global.config.CommonApiResponse;
import com.jimin.hellmap.global.config.oauth.dto.OauthAccessToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "소셜 로그인/회원가입")
@RequestMapping("api/auth")
public class MemberApiController {
    private final MemberService memberService;

    @PostMapping("google")
    @Operation(summary = "Google 회원 확인")
    public ResponseEntity<CommonApiResponse<ValidateMemberDto>> validateGoogleMember(
            @Valid @RequestBody OauthAccessToken oauthAccessToken) {
        return ResponseEntity.ok(CommonApiResponse.of(memberService.validateMember("google", oauthAccessToken)));
    }

    @PostMapping("kakao")
    @Operation(summary = "Kakao 회원 확인")
    public ResponseEntity<CommonApiResponse<ValidateMemberDto>> validateKakaoMember(
            @Valid @RequestBody OauthAccessToken oauthAccessToken) {
        return ResponseEntity.ok(CommonApiResponse.of(memberService.validateMember("kakao", oauthAccessToken)));
    }

    @PostMapping("signup")
    @Operation(summary = "소셜 회원가입")
    public ResponseEntity<CommonApiResponse<MemberResponseDto>> signUp(
            @Valid @RequestBody MemberRequestDto memberRequestDto) {
        return ResponseEntity.ok(CommonApiResponse.of(memberService.signUp(memberRequestDto)));
    }

    @GetMapping
    @Operation(summary = "닉네임 중복체크")
    public ResponseEntity<CommonApiResponse<Boolean>> checkNickname(
            @RequestParam(required = true) String nickname) {
        return ResponseEntity.ok(CommonApiResponse.of(memberService.validateNickname(nickname)));
    }
}
