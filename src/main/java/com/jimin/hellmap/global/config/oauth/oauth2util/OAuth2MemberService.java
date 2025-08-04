package com.jimin.hellmap.global.config.oauth.oauth2util;

import com.jimin.hellmap.domain.member.MemberRepository;
import com.jimin.hellmap.domain.member.entity.Member;
import com.jimin.hellmap.global.config.oauth.dto.OAuth2MemberInfo;
import com.jimin.hellmap.global.config.oauth.dto.PrincipalDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OAuth2MemberService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 유저 정보(attributes) 가져오기
        Map<String, Object> oAuth2UserAttributes = super.loadUser(userRequest).getAttributes();

        // 2. resistrationId 가져오기 (third-party id)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 3. userNameAttributeName 가져오기
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        // 4. 유저 정보 dto 생성
        OAuth2MemberInfo oAuth2UserInfo = OAuth2MemberInfo.of(registrationId, oAuth2UserAttributes);

        // 5. 회원가입 및 로그인
        Member member = getOrSave(oAuth2UserInfo, registrationId);

        // 6. OAuth2User로 반환
        return new PrincipalDetails(member, oAuth2UserAttributes, userNameAttributeName);
    }

    private Member getOrSave(OAuth2MemberInfo oAuth2UserInfo, String providerId) {
        Optional<Member> member = memberRepository.findByProviderId(oAuth2UserInfo.providerId());

        if (member.isPresent()) {
            return member.get();
        } else {
            Member newMember = oAuth2UserInfo.toEntity(oAuth2UserInfo, providerId);
            return memberRepository.save(newMember);
        }
    }
}
