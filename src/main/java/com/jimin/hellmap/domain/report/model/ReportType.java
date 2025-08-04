package com.jimin.hellmap.domain.report.model;

public enum ReportType {
    SCARY,
    ANNOYING,
    FUNNY;

    public static ReportType from(String type) {
        return switch (type) {
            case "SCARY" -> SCARY;
            case "ANNOYING" -> ANNOYING;
            case "FUNNY" -> FUNNY;
            default -> throw new IllegalArgumentException("Unknown report type: " + type);
        };
    }
}
