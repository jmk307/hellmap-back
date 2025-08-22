package com.jimin.hellmap.domain.report;

import com.jimin.hellmap.domain.member.MemberRepository;
import com.jimin.hellmap.domain.member.entity.Member;
import com.jimin.hellmap.domain.report.dto.ReportRegionResponseDto;
import com.jimin.hellmap.domain.report.dto.ReportRequestDto;
import com.jimin.hellmap.domain.report.dto.ReportResponseDto;
import com.jimin.hellmap.domain.report.dto.ReportUpdateRequestDto;
import com.jimin.hellmap.domain.report.entity.Report;
import com.jimin.hellmap.domain.report.entity.ReportRegion;
import com.jimin.hellmap.domain.report.entity.relation.ReportMember;
import com.jimin.hellmap.domain.report.model.ReportType;
import com.jimin.hellmap.domain.report.repository.ReportMemberRepository;
import com.jimin.hellmap.domain.report.repository.ReportRegionRepository;
import com.jimin.hellmap.domain.report.repository.ReportRepository;
import com.jimin.hellmap.global.config.s3.AwsS3ServiceImpl;
import com.jimin.hellmap.global.error.ErrorCode;
import com.jimin.hellmap.global.error.exception.ForbiddenException;
import com.jimin.hellmap.global.error.exception.NotFoundException;
import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final ReportMemberRepository reportMemberRepository;
    private final ReportRegionRepository reportRegionRepository;
    private final AwsS3ServiceImpl awsS3Service;
    private final MemberRepository memberRepository;
    private final WebClient webClient;

    // Mock 데이터 제보 등록
    @Transactional
    public void makeMockReports(List<ReportRequestDto> reportRequestDtos) {
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        for (ReportRequestDto reportRequestDto : reportRequestDtos) {
            ReportRegion reportRegion = reportRegionRepository.findByRegionCode(reportRequestDto.regionCode());

            Report report = Report.builder()
                    .title(reportRequestDto.title())
                    .content(reportRequestDto.content())
                    .address(reportRequestDto.address())
                    .latitude(reportRequestDto.latitude())
                    .longitude(reportRequestDto.longitude())
                    .reportType(ReportType.from(reportRequestDto.emotion()))
                    .isHot(false)
                    .isActive(true)
                    .member(member)
                    .reportRegion(reportRegion)
                    .build();
            reportRepository.save(report);
        }
    }

    // 제보 등록
    @Transactional
    public ReportResponseDto makeReport(Member member, ReportRequestDto reportRequestDto, MultipartFile imageFile) {
        String imageUrl = (imageFile != null && !imageFile.isEmpty())
                ? awsS3Service.uploadImage(imageFile, "report")
                : null;
        ReportRegion reportRegion = reportRegionRepository.findByRegionCode(reportRequestDto.regionCode());

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
                .reportRegion(reportRegion)
                .build();
        reportRepository.save(report);

        return ReportResponseDto.of(report, 0, false);
    }

    // 제보 지역 불러오기
    @Transactional(readOnly = true)
    public List<ReportRegionResponseDto> showReportRegions() {
        List<ReportRegion> reportRegions = reportRegionRepository.findAll();
        return reportRegions.stream()
                .map(region -> ReportRegionResponseDto.of(region,
                        reportRepository.countAllByReportRegionAndReportType(region, ReportType.SCARY),
                        reportRepository.countAllByReportRegionAndReportType(region, ReportType.ANNOYING),
                        reportRepository.countAllByReportRegionAndReportType(region, ReportType.FUNNY)
                        ))
                .collect(Collectors.toList());
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
    public String summaryReports() {
        List<ReportRegion> reportRegions = reportRegionRepository.findAll();

        for (ReportRegion reportRegion : reportRegions) {
            List<Report> reports = reportRepository.findAllByReportRegionAndIsActiveTrue(reportRegion);

            if (reports.size() >= 3) {
                List<ReportResponseDto> reportsOrderByLikes = reports.stream()
                        .map(report -> ReportResponseDto.of(report,
                                reportMemberRepository.countAllByReport(report), false))
                        .sorted(Comparator.comparing(ReportResponseDto::likes).reversed())
                        .limit(3)
                        .toList();

                String combinedContent = reportsOrderByLikes.stream()
                        .map(ReportResponseDto::content)
                        .collect(Collectors.joining(" "));

                if (!combinedContent.equals(reportRegion.getSummaryText())) {
                    reportRegion.updateSummary(combinedContent);
                }
            }
        }

        return "제보 요약이 완료되었습니다.";
    }

    private WebClient pollinationsClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10_000)
                .responseTimeout(Duration.ofSeconds(60));

        return WebClient.builder()
                .baseUrl("https://image.pollinations.ai")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    // AI 제보요약 이미지 생성
    @Transactional
    public String generateReportSummaryImage() {
        List<ReportRegion> reportRegions = reportRegionRepository.findAll();
        WebClient client = pollinationsClient();

        Flux.fromIterable(reportRegions)
                .filter(rr -> rr.getSummaryImageUrl() == null || rr.getSummaryImageUrl().isEmpty())
                .concatMap(rr ->
                        client.get()
                                .uri(builder -> builder
                                        .path("/prompt/{p}")                 // prompt는 path variable
                                        .queryParam("width", 512)
                                        .queryParam("height", 512)
                                        .queryParam("model", "flux")
                                        .queryParam("seed", ThreadLocalRandom.current().nextInt(10_000))
                                        .build(rr.getSummaryText()))         // UriBuilder가 인코딩 처리
                                .retrieve()
                                .onStatus(HttpStatusCode::isError,
                                        resp -> resp.createException().flatMap(Mono::error))
                                .bodyToMono(byte[].class)
                                .timeout(Duration.ofSeconds(60))
                                .map(bytes -> {
                                    // 바이트 -> MultipartFile
                                    MultipartFile mf = new MockMultipartFile(
                                            "aiImage", "ai_image.png", "image/png", bytes);

                                    // 업로드
                                    String uploadedUrl = awsS3Service.uploadImage(mf, "reportRegion");

                                    // 엔티티 업데이트 + 저장(명시 save로 안정화)
                                    rr.updateSummaryImageUrl(uploadedUrl);
                                    reportRegionRepository.save(rr);

                                    System.out.println(rr.getRegionName() + " 성공!");
                                    return rr;
                                })
                                .onErrorResume(e -> {
                                    System.out.println(rr.getRegionName() + " 실패: " + e.getMessage());
                                    return Mono.empty(); // 실패한 리전은 건너뛰기
                                })
                )
                // 요청 간 텀: 버스트 완화(랜덤 지연도 OK)
                .delayElements(Duration.ofMillis(500))
                .blockLast(); // 모두 끝날 때까지 동기 대기

        return "제보 요약 이미지 생성이 완료되었습니다.";
    }

    private String generateImageUrl(String prompt) {
        String baseUrl = "https://image.pollinations.ai/prompt/";
        String encodedPrompt = URLEncoder.encode(prompt, StandardCharsets.UTF_8);
        int randomSeed = (int) (Math.random() * 10000);

        return baseUrl + encodedPrompt + "?width=512&height=512&model=flux&seed=" + randomSeed;
    }
}
