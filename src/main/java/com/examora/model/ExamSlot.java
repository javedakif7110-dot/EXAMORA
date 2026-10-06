package com.examora.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a date and session slot for examinations.
 */
public record ExamSlot(
        String slotId,
        LocalDate examDate,
        SessionType session // MORNING or AFTERNOON
) {
    public enum SessionType {
        MORNING("09:30 AM - 12:30 PM"),
        AFTERNOON("01:30 PM - 04:30 PM");

        private final String timing;

        SessionType(String timing) {
            this.timing = timing;
        }

        public String getTiming() {
            return timing;
        }
    }

    public ExamSlot {
        Objects.requireNonNull(slotId, "Slot ID cannot be null");
        Objects.requireNonNull(examDate, "Exam date cannot be null");
        Objects.requireNonNull(session, "Session type cannot be null");
    }
}
