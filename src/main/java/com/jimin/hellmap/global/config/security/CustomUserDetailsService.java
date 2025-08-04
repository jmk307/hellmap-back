package com.jimin.hellmap.global.config.security;

import com.jimin.hellmap.domain.member.MemberRepository;
import com.jimin.hellmap.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String providerId) {
        return memberRepository.findByProviderId(providerId)
                .map(this::createUser)
                .orElseThrow(() -> new UsernameNotFoundException(providerId + " -> 데이터베이스에서 찾을 수 없습니다."));
    }

    private User createUser(Member member) {
        return new User(member.getProviderId(), member.getNickname(), authorities());
    }

    private static Collection<? extends GrantedAuthority> authorities() {
        Collection<GrantedAuthority> role = new ArrayList<>();
        role.add(new SimpleGrantedAuthority("ROLE_USER"));
        return role;
    }
}
