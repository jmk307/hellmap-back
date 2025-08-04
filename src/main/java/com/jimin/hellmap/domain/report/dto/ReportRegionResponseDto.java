package com.jimin.hellmap.domain.report.dto;

import com.jimin.hellmap.domain.report.entity.ReportRegion;
import com.jimin.hellmap.domain.report.model.ReportType;
import lombok.Builder;

import java.util.Map;

@Builder
public record ReportRegionResponseDto(
        String region,
        Integer totalReports,
        Long scaryCount,
        Long annoyingCount,
        Long funnyCount,
        ReportType dominantEmotion,
        String aiImageUrl,
        Integer hellLevel
) {
    public static ReportRegionResponseDto of(
            ReportRegion reportRegion,
            Long scaryCount,
            Long annoyingCount,
            Long funnyCount
    ) {
        int totalReports = reportRegion.getRegionReports().size();

        return ReportRegionResponseDto.builder()
                .region(reportRegion.getRegionName())
                .totalReports(totalReports)
                .scaryCount(scaryCount)
                .annoyingCount(annoyingCount)
                .funnyCount(funnyCount)
                .dominantEmotion(getDominantEmotion(scaryCount, annoyingCount, funnyCount))
                .aiImageUrl(reportRegion.getSummaryImageUrl())
                .hellLevel(calculateHellLevel(scaryCount, annoyingCount, funnyCount, totalReports))
                .build();
    }

    private static ReportType getDominantEmotion(Long scaryCount, Long annoyingCount, Long funnyCount) {
        Map<ReportType, Long> emotionStats = Map.of(
                ReportType.SCARY, scaryCount,
                ReportType.ANNOYING, annoyingCount,
                ReportType.FUNNY, funnyCount
        );

        return emotionStats.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(ReportType.SCARY); // 기본값 설정
    }

    private static Integer calculateHellLevel(Long scaryCount, Long annoyingCount, Long funnyCount, Integer totalReports) {
        double weightedSum = scaryCount * 1.5 + annoyingCount * 1.2 + funnyCount * 0.5;
        int calculatedLevel = (int) Math.round((weightedSum / totalReports) * 2);
        return Math.min(5, Math.max(1, calculatedLevel));
    }
}
