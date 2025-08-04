package com.jimin.hellmap.domain.report.repository;

import com.jimin.hellmap.domain.report.entity.Report;
import com.jimin.hellmap.domain.report.entity.ReportRegion;
import com.jimin.hellmap.domain.report.model.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    @Query("SELECT r FROM Report r WHERE r.createdAt >= :cutoffTime")
    List<Report> findRecentReports(@Param("cutoffTime") LocalDateTime cutoffTime);

    @Modifying
    @Query("UPDATE Report r SET r.isHot = :isHot WHERE r.reportId = :reportId")
    void updateHotStatus(@Param("reportId") Long reportId, @Param("isHot") boolean isHot);

    @Modifying
    @Query("UPDATE Report r SET r.isHot = false WHERE r.createdAt < :cutoffTime AND r.isHot = true")
    void updateOldReportsToNotHot(@Param("cutoffTime") LocalDateTime cutoffTime);

    List<Report> findAllByIsActiveTrueOrderByCreatedAtDesc();

    Optional<Report> findByReportIdAndIsActiveTrue(Long reportId);

    List<Report> findAllByReportRegionAndIsActiveTrue(ReportRegion reportRegion);

    Long countAllByReportRegionAndReportType(ReportRegion reportRegion, ReportType reportType);
}
