package com.jimin.hellmap.domain.report.entity.relation;

import com.jimin.hellmap.domain.member.entity.Member;
import com.jimin.hellmap.domain.report.entity.Report;
import com.jimin.hellmap.global.config.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReportMember extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Report report;

    @Builder
    public ReportMember(Member member, Report report) {
        this.member = member;
        this.report = report;
    }
}
