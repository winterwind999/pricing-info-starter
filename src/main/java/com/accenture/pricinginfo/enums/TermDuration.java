package com.accenture.pricinginfo.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TermDuration {
    THREE_MONTHS("3_MONTHS"),
    SIX_MONTHS("6_MONTHS"),
    NINE_MONTHS("9_MONTHS"),
    ONE_YEAR("1_YEAR"),
    TWO_YEARS("2_YEARS"),
    THREE_YEARS("3_YEARS");

    private final String value;

    TermDuration(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static TermDuration fromValue(String value) {
        for (TermDuration duration : TermDuration.values()) {
            if (duration.value.equalsIgnoreCase(value)) {
                return duration;
            }
        }
        throw new IllegalArgumentException("Unknown term duration: " + value);
    }
}
