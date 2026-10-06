package com.examora.model;

import java.util.List;
import java.util.Objects;

/**
 * Encapsulates the results of the Minimum-Disruption Recovery Engine execution.
 */
public record RecoveryResult(
        String recoveryId,
        String status, // "RECOVERED", "PARTIAL", "FAILED"
        int totalStudents,
        int affectedStudents,
        int movedStudents,
        int unchangedStudents,
        int hardViolations,
        double executionTimeMs,
        double initialPlanningTimeMs,
        String aiExplanation,
        List<Assignment> recoveredAssignments,
        List<ReassignmentDetail> reassignments
) {
    public record ReassignmentDetail(
            String studentId,
            String studentName,
            String examId,
            String oldHallId,
            String oldSeatId,
            String newHallId,
            String newSeatId,
            String reason
    ) {}

    public RecoveryResult {
        Objects.requireNonNull(recoveryId, "Recovery ID cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");
        recoveredAssignments = List.copyOf(recoveredAssignments != null ? recoveredAssignments : List.of());
        reassignments = List.copyOf(reassignments != null ? reassignments : List.of());
    }
}
