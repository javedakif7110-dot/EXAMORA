package com.examora.model;

import java.util.List;
import java.util.Objects;

/**
 * Represents a student registered in the EXAMORA platform.
 * Demonstrates Java Record usage (Java 21) for immutable domain models.
 *
 * @param id Student ID (e.g., "STU-1001")
 * @param name Student Full Name
 * @param department Department (e.g., "CSE", "ECE", "MECH")
 * @param year Academic Year (1 to 4)
 * @param registeredExamIds List of Exam IDs registered by this student
 * @param requiresAccessibility True if student requires special accessibility seating
 */
public record Student(
        String id,
        String name,
        String department,
        int year,
        List<String> registeredExamIds,
        boolean requiresAccessibility
) {
    public Student {
        Objects.requireNonNull(id, "Student ID cannot be null");
        Objects.requireNonNull(name, "Student Name cannot be null");
        Objects.requireNonNull(department, "Department cannot be null");
        registeredExamIds = List.copyOf(registeredExamIds != null ? registeredExamIds : List.of());
    }
}
