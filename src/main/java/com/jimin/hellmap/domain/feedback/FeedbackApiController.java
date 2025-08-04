package com.jimin.hellmap.domain.feedback;

import com.jimin.hellmap.domain.feedback.dto.FeedbackRequestDto;
import com.jimin.hellmap.global.config.CommonApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "피드백")
@RequestMapping("api/feedbacks")
public class FeedbackApiController {
    private final FeedbackService feedbackService;

    @PostMapping
    @Operation(summary = "피드백 제보하기")
    public ResponseEntity<CommonApiResponse<String>> checkNickname(
            @RequestBody FeedbackRequestDto feedbackRequestDto) {
        return ResponseEntity.ok(CommonApiResponse.of(feedbackService.makeFeedback(feedbackRequestDto)));
    }
}
