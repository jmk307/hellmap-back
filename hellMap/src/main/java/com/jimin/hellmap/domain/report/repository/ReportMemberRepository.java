package com.jimin.hellmap.domain.report.repository;

import com.jimin.hellmap.domain.member.entity.Member;
import com.jimin.hellmap.domain.report.entity.Report;
import com.jimin.hellmap.domain.report.entity.relation.ReportMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportMemberRepository extends JpaRepository<ReportMember, Long> {
    Integer countAllByReport(Report report);

    Optional<ReportMember> findByMemberAndReport(Member member, Report report);

    boolean existsByMemberAndReport(Member member, Report report);
}
