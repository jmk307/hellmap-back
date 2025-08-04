package com.jimin.hellmap.domain.feedback.model;

public enum FeedbackType {
    FEATURE,
    BUG,
    IMPROVEMENT,
    OTHER;

    public static FeedbackType from(String feedbackType) {
        return switch (feedbackType.toUpperCase()) {
            case "FEATURE" -> FEATURE;
            case "BUG" -> BUG;
            case "IMPROVEMENT" -> IMPROVEMENT;
            case "OTHER" -> OTHER;
            default -> throw new IllegalArgumentException("Unknown feedback type: " + feedbackType);
        };
    }
}
