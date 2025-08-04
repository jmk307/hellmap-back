package com.jimin.hellmap.domain.feedback.dto;

import com.jimin.hellmap.domain.feedback.model.FeedbackType;
import com.jimin.hellmap.domain.feedback.model.Priority;
import lombok.Builder;

public record FeedbackRequestDto(
        String feedbackType,
        String title,
        String description,
        String priority
) {
}
