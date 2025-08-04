package com.jimin.hellmap.domain.member;

import aj.org.objectweb.asm.commons.Remapper;
import com.jimin.hellmap.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByProviderId(String providerId);

    boolean existsByNickname(String nickname);
}
