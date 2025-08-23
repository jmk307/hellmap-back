package com.jimin.hellmap.domain.feedback;

import com.jimin.hellmap.domain.feedback.dto.FeedbackRequestDto;
import com.jimin.hellmap.domain.feedback.dto.FeedbackResponseDto;
import com.jimin.hellmap.domain.feedback.dto.FeedbackReviewDto;
import com.jimin.hellmap.domain.feedback.entity.Feedback;
import com.jimin.hellmap.domain.feedback.model.FeedbackType;
import com.jimin.hellmap.domain.feedback.model.Priority;
import com.jimin.hellmap.domain.member.entity.Member;
import com.jimin.hellmap.global.error.ErrorCode;
import com.jimin.hellmap.global.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;

    // 피드백 생성
    @Transactional
    public String makeFeedback(FeedbackRequestDto feedbackRequestDto) {
        Feedback feedback = Feedback.builder()
                .feedbackType(FeedbackType.from(feedbackRequestDto.feedbackType()))
                .title(feedbackRequestDto.title())
                .description(feedbackRequestDto.description())
                .priority(Priority.from(feedbackRequestDto.priority()))
                .build();
        feedbackRepository.save(feedback);

        return "피드백이 성공적으로 등록되었습니다.";
    }

    // 피드백 게시판 불러오기
    @Transactional(readOnly = true)
    public List<FeedbackResponseDto> showFeedbacks() {
        List<Feedback> feedbacks = feedbackRepository.findAll();

        return feedbacks.stream()
                .map(FeedbackResponseDto::of)
                .toList();
    }

    // 피드백 리뷰하기
    @Transactional
    public String reviewFeedback(Member member, Long feedbackId, FeedbackReviewDto feedbackReviewDto) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.REPORT_NOT_FOUND));

        if (member.getMemberId() != 1L) {
            throw new NotFoundException(ErrorCode.FORBIDDEN_USER);
        }

        feedback.updateReview(feedbackReviewDto.status(), feedbackReviewDto.review());

        return "피드백 리뷰가 성공적으로 업데이트되었습니다.";
    }
}
