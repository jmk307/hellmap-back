package com.jimin.hellmap.domain.report.util;

import com.jimin.hellmap.domain.report.repository.ReportMemberRepository;
import com.jimin.hellmap.domain.report.repository.ReportRepository;
import com.jimin.hellmap.domain.report.entity.Report;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HotScoreCalculator {
    private final ReportMemberRepository reportMemberRepository;
    private final ReportRepository reportRepository;

    // 상수 정의
    private static final int HOT_HOURS = 72;           // 최근 3일
    private static final int HOT_LIKES_THRESHOLD = 7; // 핫 판정 좋아요 기준
    private static final Duration HOT_DURATION = Duration.ofHours(48);

    public boolean isHot(Report report) {
        // 1. 시간 체크: 72시간 이내인가?
        if (!isRecent(report.getCreatedAt())) {
            return false;
        }

        // 2. 좋아요 체크: 기준값 이상인가?
        return reportMemberRepository.countAllByReport(report) >= HOT_LIKES_THRESHOLD;
    }

    private boolean isRecent(LocalDateTime createdAt) {
        long hoursAgo = ChronoUnit.HOURS.between(createdAt, LocalDateTime.now());
        return hoursAgo <= HOT_HOURS;
    }

    @Scheduled(cron = "0 0 */3 * * *", zone = "Asia/Seoul") // 3시간마다 실행
    @Transactional
    public void updateHotStatus() {
        LocalDateTime cutoffTime = LocalDateTime.now().minus(HOT_DURATION);

        // 최근 48시간 내의 제보만 검사
        List<Report> reports = reportRepository.findRecentReports(cutoffTime);

        for (Report report : reports) {
            boolean isHot = isHot(report);
            reportRepository.updateHotStatus(report.getReportId(), isHot);
        }

        // 48시간 이상 된 제보들은 모두 일반으로 변경
        reportRepository.updateOldReportsToNotHot(cutoffTime);
    }
}
