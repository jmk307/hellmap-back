package com.jimin.hellmap.domain.report;

import com.jimin.hellmap.domain.member.entity.Member;
import com.jimin.hellmap.domain.report.dto.ReportRequestDto;
import com.jimin.hellmap.domain.report.dto.ReportResponseDto;
import com.jimin.hellmap.domain.report.dto.ReportUpdateRequestDto;
import com.jimin.hellmap.domain.report.entity.Report;
import com.jimin.hellmap.domain.report.entity.relation.ReportMember;
import com.jimin.hellmap.domain.report.model.ReportType;
import com.jimin.hellmap.domain.report.repository.ReportMemberRepository;
import com.jimin.hellmap.domain.report.repository.ReportRepository;
import com.jimin.hellmap.global.config.s3.AwsS3ServiceImpl;
import com.jimin.hellmap.global.error.ErrorCode;
import com.jimin.hellmap.global.error.exception.ForbiddenException;
import com.jimin.hellmap.global.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final ReportMemberRepository reportMemberRepository;
    private final AwsS3ServiceImpl awsS3Service;

    // 제보 등록
    @Transactional
    public ReportResponseDto makeReport(Member member, ReportRequestDto reportRequestDto, MultipartFile imageFile) {
        String imageUrl = (imageFile != null && !imageFile.isEmpty())
                ? awsS3Service.uploadImage(imageFile, "report")
                : null;

        Report report = Report.builder()
                .title(reportRequestDto.title())
                .content(reportRequestDto.content())
                .address(reportRequestDto.address())
                .latitude(reportRequestDto.latitude())
                .longitude(reportRequestDto.longitude())
                .imageUrl(imageUrl)
                .reportType(ReportType.from(reportRequestDto.emotion()))
                .isHot(false)
                .isActive(true)
                .member(member)
                .build();
        reportRepository.save(report);

        return ReportResponseDto.of(report, 0, false);
    }

    // 제보 피드 불러오기
    @Transactional(readOnly = true)
    public List<ReportResponseDto> showReportFeeds(Member member) {
        List<Report> reports = reportRepository.findAllByIsActiveTrueOrderByCreatedAtDesc();
        return reports.stream()
                .map(report -> ReportResponseDto.of(report,
                        reportMemberRepository.countAllByReport(report),
                        reportMemberRepository.existsByMemberAndReport(member, report)))
                .toList();
    }

    // 제보 수정
    @Transactional
    public ReportResponseDto modReport(Member member, Long reportId, ReportUpdateRequestDto reportUpdateRequestDto, MultipartFile imageFile) {
        Report report = reportRepository.findByReportIdAndIsActiveTrue(reportId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.REPORT_NOT_FOUND));
        Integer likes = reportMemberRepository.countAllByReport(report);
        boolean isLike = reportMemberRepository.existsByMemberAndReport(member, report);

        if (!report.getMember().equals(member)) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_USER);
        }

        String imageUrl = report.getImageUrl();
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = awsS3Service.uploadImage(imageFile, "report");
        } else {
            if (!reportUpdateRequestDto.keepExistingImage()) {
                imageUrl = null;
            }
        }

        report.update(
                reportUpdateRequestDto.title(),
                reportUpdateRequestDto.content(),
                reportUpdateRequestDto.address(),
                reportUpdateRequestDto.latitude(),
                reportUpdateRequestDto.longitude(),
                imageUrl,
                ReportType.from(reportUpdateRequestDto.emotion())
        );

        return ReportResponseDto.of(report, likes, isLike);
    }

    // 제보 삭제
    @Transactional
    public String delReport(Member member, Long reportId) {
        Report report = reportRepository.findByReportIdAndIsActiveTrue(reportId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.REPORT_NOT_FOUND));

        if (!report.getMember().equals(member)) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_USER);
        }

        report.deactivate();

        return "제보가 삭제되었습니다.";
    }

    // 제보 좋아요 / 취소
    @Transactional
    public String reportLike(Member member, Long reportId) {
        Report report = reportRepository.findByReportIdAndIsActiveTrue(reportId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.REPORT_NOT_FOUND));

        Optional<ReportMember> checkReportMember = reportMemberRepository.findByMemberAndReport(member, report);
        if (checkReportMember.isPresent()) {
            // 이미 좋아요를 누른 경우, 좋아요 취소
            reportMemberRepository.delete(checkReportMember.get());
            return "좋아요가 취소되었습니다.";
        } else {
            // 좋아요를 누르지 않은 경우, 좋아요 추가
            ReportMember reportMember = ReportMember.builder()
                    .member(member)
                    .report(report)
                    .build();
            reportMemberRepository.save(reportMember);
            return "좋아요가 추가되었습니다.";
        }
    }

    // 제보 요약
    @Transactional
    public void summaryReports() {

    }
}
