package com.examora.model;

import java.util.Objects;

/**
 * Represents an assigned seating allocation for a student taking an exam in a specific slot, hall, and seat.
 */
public record Assignment(
        String assignmentId,
        String studentId,
        String examId,
        String slotId,
        String hallId,
        String seatId,
        String invigilatorId
) {
    public Assignment {
        Objects.requireNonNull(assignmentId, "Assignment ID cannot be null");
        Objects.requireNonNull(studentId, "Student ID cannot be null");
        Objects.requireNonNull(examId, "Exam ID cannot be null");
        Objects.requireNonNull(slotId, "Slot ID cannot be null");
        Objects.requireNonNull(hallId, "Hall ID cannot be null");
        Objects.requireNonNull(seatId, "Seat ID cannot be null");
    }
}
