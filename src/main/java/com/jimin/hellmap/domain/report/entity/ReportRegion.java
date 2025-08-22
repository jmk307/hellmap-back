package com.jimin.hellmap.domain.report.entity;

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
public class ReportRegion extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportRegionId;

    private Long regionCode;

    private String regionName;

    private Double latitude;

    private Double longitude;

    @Column(length = 10000)
    private String summaryText;

    @Column(length = 1000)
    private String imagePromptText;

    @Column(length = 1000)
    private String summaryImageUrl;

    @OneToMany(mappedBy = "reportRegion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Report> regionReports;

    @Builder
    public ReportRegion(Long regionCode, String regionName, Double latitude, Double longitude, String summaryText, String imagePromptText, String summaryImageUrl) {
        this.regionCode = regionCode;
        this.regionName = regionName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.summaryText = summaryText;
        this.summaryImageUrl = summaryImageUrl;
    }

    public void updateSummary(String summaryText) {
        this.summaryText = summaryText;
        this.imagePromptText = null;
    }

    public void updateSummaryImageUrl(String summaryImageUrl) {
        this.summaryImageUrl = summaryImageUrl;
    }
}
