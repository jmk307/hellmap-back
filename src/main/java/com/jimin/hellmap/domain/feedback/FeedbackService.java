package com.jimin.hellmap.domain.feedback;

import com.jimin.hellmap.domain.feedback.dto.FeedbackRequestDto;
import com.jimin.hellmap.domain.feedback.dto.FeedbackResponseDto;
import com.jimin.hellmap.domain.feedback.entity.Feedback;
import com.jimin.hellmap.domain.feedback.model.FeedbackType;
import com.jimin.hellmap.domain.feedback.model.Priority;
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

    @Transactional(readOnly = true)
    public List<FeedbackResponseDto> showFeedbacks() {
        List<Feedback> feedbacks = feedbackRepository.findAll();

        return feedbacks.stream()
                .map(FeedbackResponseDto::of)
                .toList();
    }
}
