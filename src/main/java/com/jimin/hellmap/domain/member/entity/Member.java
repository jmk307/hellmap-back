package com.jimin.hellmap.domain.member.entity;

import com.jimin.hellmap.domain.feedback.entity.Feedback;
import com.jimin.hellmap.domain.member.model.Social;
import com.jimin.hellmap.domain.report.entity.Report;
import com.jimin.hellmap.global.config.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private Social provider;

    private String providerId;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Report> reports = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Feedback> feedbacks= new ArrayList<>();

    @Builder
    public Member(String nickname, Social provider, String providerId, List<Report> reports, List<Feedback> feedbacks) {
        this.nickname = nickname;
        this.provider = provider;
        this.providerId = providerId;
        this.reports = reports != null ? reports : new ArrayList<>();
        this.feedbacks = feedbacks != null ? feedbacks : new ArrayList<>();
    }
}
