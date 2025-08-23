package com.jimin.hellmap.domain.feedback.dto;

import com.jimin.hellmap.domain.feedback.entity.Feedback;
import com.jimin.hellmap.domain.feedback.model.FeedbackType;
import com.jimin.hellmap.domain.feedback.model.Priority;
import com.jimin.hellmap.domain.feedback.model.Status;
import com.jimin.hellmap.global.util.Time;
import lombok.Builder;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Builder
public record FeedbackResponseDto(
        Long feedbackId,
        FeedbackType feedbackType,
        String title,
        String description,
        Priority priority,
        Status status,
        String createdAt,
        String author
) {
    public static FeedbackResponseDto of(Feedback feedback) {
        String createdAt = feedback.getCreatedAt().toString();
        LocalDateTime dateTime = LocalDateTime.parse(createdAt);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일");
        String formatted = dateTime.format(formatter);

        return FeedbackResponseDto.builder()
                .feedbackId(feedback.getFeedbackId())
                .feedbackType(feedback.getFeedbackType())
                .title(feedback.getTitle())
                .description(feedback.getDescription())
                .priority(feedback.getPriority())
                .status(feedback.getStatus())
                .createdAt(formatted)
                .author(feedback.getMember().getNickname())
                .build();
    }
}
