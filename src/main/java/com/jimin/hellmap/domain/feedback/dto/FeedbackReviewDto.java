package com.jimin.hellmap.domain.feedback.dto;

import com.jimin.hellmap.domain.feedback.model.Status;

public record FeedbackReviewDto(
        Status status,
        String review
) {
}
