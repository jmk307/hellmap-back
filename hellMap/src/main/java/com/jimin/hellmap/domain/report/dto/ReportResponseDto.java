package com.jimin.hellmap.domain.report.dto;

import com.jimin.hellmap.domain.report.entity.Report;
import com.jimin.hellmap.global.util.Time;
import lombok.Builder;

import java.sql.Timestamp;

@Builder
public record ReportResponseDto(
        Long reportId,
        String emotion,
        String title,
        String content,
        String location,
        Double latitude,
        Double longitude,
        String timeAgo,
        Integer likes,
        boolean isLike,
        boolean isHot,
        String imageUrl,
        String author
) {
    public static ReportResponseDto of(Report report, Integer likes, boolean isLike) {
        return ReportResponseDto.builder()
                .reportId(report.getReportId())
                .emotion(report.getReportType().name())
                .title(report.getTitle())
                .content(report.getContent())
                .latitude(report.getLatitude())
                .longitude(report.getLongitude())
                .location(report.getAddress())
                .timeAgo(Time.calculateTime(Timestamp.valueOf(report.getCreatedAt())))
                .likes(likes)
                .isLike(isLike)
                .isHot(report.isHot())
                .imageUrl(report.getImageUrl())
                .author(report.getMember().getNickname())
                .build();
    }
}
