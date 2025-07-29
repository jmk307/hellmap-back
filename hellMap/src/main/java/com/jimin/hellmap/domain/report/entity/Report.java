package com.jimin.hellmap.domain.report.entity;

import com.jimin.hellmap.domain.member.entity.Member;
import com.jimin.hellmap.domain.report.entity.relation.ReportMember;
import com.jimin.hellmap.domain.report.model.ReportType;
import com.jimin.hellmap.global.config.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @Column(length = 100)
    private String title;

    @Column(length = 1000)
    private String content;

    private String address;

    private Double latitude;

    private Double longitude;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    private boolean isHot;

    private boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private ReportRegion reportRegion;

    @OneToMany(mappedBy = "report", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ReportMember> reportMembers;

    @Builder
    public Report(String title, String content, String address,
                  Double latitude, Double longitude, String imageUrl,
                  ReportType reportType, boolean isHot, boolean isActive, Member member, ReportRegion reportRegion) {
        this.title = title;
        this.content = content;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.reportType = reportType;
        this.isHot = isHot;
        this.isActive = isActive;
        this.member = member;
        this.reportRegion = reportRegion;
    }

    public void update(String title, String content, String address, Double latitude, Double longitude, String imageUrl, ReportType reportType) {
        this.title = title;
        this.content = content;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.reportType = reportType;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
