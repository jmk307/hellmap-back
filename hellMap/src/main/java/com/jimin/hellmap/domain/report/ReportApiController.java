package com.jimin.hellmap.domain.report;

import com.jimin.hellmap.domain.member.entity.Member;
import com.jimin.hellmap.domain.report.dto.ReportRequestDto;
import com.jimin.hellmap.domain.report.dto.ReportResponseDto;
import com.jimin.hellmap.domain.report.dto.ReportUpdateRequestDto;
import com.jimin.hellmap.global.config.CommonApiResponse;
import com.jimin.hellmap.global.config.security.jwt.annotation.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Tag(name = "제보하기")
@RequestMapping("api/reports")
public class ReportApiController {
    private final ReportService reportService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "제보하기")
    public ResponseEntity<CommonApiResponse<ReportResponseDto>> makeReport(
            @Parameter(hidden = true) @LoginUser Member member,
            @Valid @RequestPart ReportRequestDto reportRequestDto,
            @RequestPart (value = "imageFile", required = false) MultipartFile imageFile) {
        return ResponseEntity.ok(CommonApiResponse.of(reportService.makeReport(member, reportRequestDto, imageFile)));
    }

    @GetMapping
    @Operation(summary = "제보 피드 불러오기")
    public ResponseEntity<CommonApiResponse<List<ReportResponseDto>>> showReportFeeds(
            @Parameter(hidden = true) @LoginUser Member member
    ) {
        return ResponseEntity.ok(CommonApiResponse.of(reportService.showReportFeeds(member)));
    }

    @PutMapping(path = "{reportId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "제보 수정하기")
    public ResponseEntity<CommonApiResponse<ReportResponseDto>> modReport(
            @Parameter(hidden = true) @LoginUser Member member,
            @PathVariable Long reportId,
            @Valid @RequestPart ReportUpdateRequestDto reportUpdateRequestDto,
            @RequestPart (value = "imageFile", required = false) MultipartFile imageFile) {
        return ResponseEntity.ok(CommonApiResponse.of(reportService.modReport(member, reportId, reportUpdateRequestDto, imageFile)));
    }

    @PatchMapping("{reportId}")
    @Operation(summary = "제보 삭제하기")
    public ResponseEntity<CommonApiResponse<String>> deleteReport(
            @Parameter(hidden = true) @LoginUser Member member,
            @PathVariable Long reportId) {
        return ResponseEntity.ok(CommonApiResponse.of(reportService.delReport(member, reportId)));
    }

    @PostMapping("{reportId}")
    @Operation(summary = "제보 좋아요")
    public ResponseEntity<CommonApiResponse<String>> likeReport(
            @Parameter(hidden = true) @LoginUser Member member,
            @PathVariable Long reportId) {
        return ResponseEntity.ok(CommonApiResponse.of(reportService.reportLike(member, reportId)));
    }

    @PatchMapping("summary")
    @Operation(summary = "제보 요약하기")
    public ResponseEntity<CommonApiResponse<String>> summaryReports() {
        return ResponseEntity.ok(CommonApiResponse.of(reportService.summaryReports()));
    }
}
