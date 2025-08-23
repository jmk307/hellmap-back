package com.jimin.hellmap.domain.feedback;

import com.jimin.hellmap.domain.feedback.dto.FeedbackRequestDto;
import com.jimin.hellmap.domain.feedback.dto.FeedbackResponseDto;
import com.jimin.hellmap.domain.feedback.dto.FeedbackReviewDto;
import com.jimin.hellmap.domain.member.entity.Member;
import com.jimin.hellmap.global.config.CommonApiResponse;
import com.jimin.hellmap.global.config.security.jwt.annotation.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "피드백")
@RequestMapping("api/feedbacks")
public class FeedbackApiController {
    private final FeedbackService feedbackService;

    @PostMapping
    @Operation(summary = "피드백 제보하기")
    public ResponseEntity<CommonApiResponse<String>> makeFeedback(
            @RequestBody FeedbackRequestDto feedbackRequestDto) {
        return ResponseEntity.ok(CommonApiResponse.of(feedbackService.makeFeedback(feedbackRequestDto)));
    }

    @GetMapping
    @Operation(summary = "피드백 불러오기")
    public ResponseEntity<CommonApiResponse<List<FeedbackResponseDto>>> showFeedbacks() {
        return ResponseEntity.ok(CommonApiResponse.of(feedbackService.showFeedbacks()));
    }

    @PatchMapping("{feedbackId}")
    @Operation(summary = "피드백 리뷰하기")
    public ResponseEntity<CommonApiResponse<String>> reviewFeedback(
            @Parameter(hidden = true) @LoginUser Member member,
            @PathVariable Long feedbackId,
            @RequestBody FeedbackReviewDto feedbackReviewDto) {
        return ResponseEntity.ok(CommonApiResponse.of(feedbackService.reviewFeedback(member, feedbackId, feedbackReviewDto)));
    }
}
