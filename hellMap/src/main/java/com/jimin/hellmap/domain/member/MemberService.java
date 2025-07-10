package com.jimin.hellmap.domain.member;

import com.jimin.hellmap.domain.member.dto.MemberRequestDto;
import com.jimin.hellmap.domain.member.dto.MemberResponseDto;
import com.jimin.hellmap.domain.member.dto.ValidateMemberDto;
import com.jimin.hellmap.domain.member.entity.Member;
import com.jimin.hellmap.domain.member.model.Social;
import com.jimin.hellmap.global.config.oauth.GoogleUtils;
import com.jimin.hellmap.global.config.oauth.KakaoUtils;
import com.jimin.hellmap.global.config.oauth.dto.GoogleMemberInfoDto;
import com.jimin.hellmap.global.config.oauth.dto.KakaoMemberDto;
import com.jimin.hellmap.global.config.oauth.dto.OauthAccessToken;
import com.jimin.hellmap.global.config.security.jwt.TokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final KakaoUtils kakaoUtils;
    private final GoogleUtils googleUtils;
    private final TokenProvider tokenProvider;

    // 회원 존재 여부 확인 및 AccessToken 발급
    @Transactional
    public ValidateMemberDto validateMember(String provider, OauthAccessToken oauthAccessToken) {
        Social social = Social.from(provider);
        String providerId = "";

        if (social == Social.KAKAO) {
            KakaoMemberDto kakaoMemberDto = kakaoUtils.getKakaoMemberInfo(oauthAccessToken);
            providerId = kakaoMemberDto.getId().toString();
        } else {
            GoogleMemberInfoDto googleMemberInfoDto = googleUtils.getGoogleMemberInfo(oauthAccessToken);
            providerId = googleMemberInfoDto.getSub();
        }

        Optional<Member> member = memberRepository.findByProviderId(providerId);

        if (member.isEmpty()) {
            return ValidateMemberDto.of(social, providerId);
        } else {
            String accessToken = tokenProvider.generateAccessToken(providerId);
            return ValidateMemberDto.of(social, member.get().getNickname(), accessToken);
        }
    }

    // 회원가입
    @Transactional
    public MemberResponseDto signUp(MemberRequestDto memberRequestDto) {
        Social provider = Social.from(memberRequestDto.provider());

        Member member = Member.builder()
                .nickname(memberRequestDto.nickname())
                .provider(provider)
                .providerId(memberRequestDto.providerId())
                .build();
        memberRepository.save(member);

        String accessToken = tokenProvider.generateAccessToken(memberRequestDto.providerId());

        return MemberResponseDto.of(memberRequestDto.nickname(), accessToken);
    }

    // 닉네임 중복 확인
    @Transactional
    public boolean validateNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }
}
