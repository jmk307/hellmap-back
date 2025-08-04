package com.jimin.hellmap.domain.member.model;

public enum Social {
    KAKAO,
    GOOGLE;

    public static Social from(String provider) {
        return switch (provider) {
            case "kakao" -> KAKAO;
            case "google" -> GOOGLE;
            default -> throw new IllegalArgumentException("Invalid provider: " + provider);
        };
    }
}
