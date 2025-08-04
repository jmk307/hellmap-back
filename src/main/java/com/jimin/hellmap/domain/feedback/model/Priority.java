package com.jimin.hellmap.domain.feedback.model;

import com.jimin.hellmap.domain.report.model.ReportType;

public enum Priority {
    LOW,
    MEDIUM,
    HIGH;

    public static Priority from(String priority) {
        return switch (priority.toUpperCase()) {
            case "LOW" -> LOW;
            case "MEDIUM" -> MEDIUM;
            case "HIGH" -> HIGH;
            default -> throw new IllegalArgumentException("Unknown report type: " + priority);
        };
    }
}
