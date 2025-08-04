package com.jimin.hellmap.domain.feedback.entity;

import com.jimin.hellmap.domain.feedback.model.FeedbackType;
import com.jimin.hellmap.domain.feedback.model.Priority;
import com.jimin.hellmap.global.config.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feedback extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackId;

    @Enumerated(EnumType.STRING)
    private FeedbackType feedbackType;

    @Column(length = 100)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Builder
    public Feedback(FeedbackType feedbackType, String title, String description, Priority priority) {
        this.feedbackType = feedbackType;
        this.title = title;
        this.description = description;
        this.priority = priority;
    }
}
