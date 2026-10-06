package com.examora.model;

import java.util.Objects;

/**
 * Represents an Examination course in EXAMORA.
 *
 * @param id Exam code (e.g., "CS301")
 * @param courseTitle Name of the course (e.g., "Java Programming")
 * @param department Department offering the exam
 * @param durationMinutes Exam duration in minutes (e.g., 180)
 */
public record Exam(
        String id,
        String courseTitle,
        String department,
        int durationMinutes
) {
    public Exam {
        Objects.requireNonNull(id, "Exam ID cannot be null");
        Objects.requireNonNull(courseTitle, "Course title cannot be null");
        Objects.requireNonNull(department, "Department cannot be null");
    }
}
