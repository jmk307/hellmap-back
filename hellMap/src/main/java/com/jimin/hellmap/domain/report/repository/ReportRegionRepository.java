package com.jimin.hellmap.domain.report.repository;

import com.jimin.hellmap.domain.report.entity.ReportRegion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRegionRepository extends JpaRepository<ReportRegion, Long> {
    ReportRegion findByRegionCode(Long regionCode);
}
