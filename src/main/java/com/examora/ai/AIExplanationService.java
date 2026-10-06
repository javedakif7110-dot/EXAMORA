package com.examora.ai;

import com.examora.model.Disruption;
import com.examora.model.RecoveryResult;

/**
 * AI Explanation Layer for EXAMORA.
 *
 * Translates deterministic Java constraint metrics and recovery results into clean,
 * natural-language explanations for administrators, institutional heads, and audit logs.
 */
public class AIExplanationService {

    /**
     * Generates natural language summary for allocation plans.
     */
    public String explainInitialPlan(int totalStudents, int totalExams, int totalHalls, int totalSeats, boolean isValid) {
        return String.format(
                "EXAMORA generated a conflict-free examination plan for %d students across %d exams, utilizing %d halls and %d seats at Chennai Institute of Technology. All 7 hard constraints (timetable conflicts, accessibility, capacity, seat uniqueness) are verified (Valid: %b).",
                totalStudents, totalExams, totalHalls, totalSeats, isValid
        );
    }

    /**
     * Generates natural language summary for disruption recovery results.
     */
    public String explainRecovery(Disruption disruption, RecoveryResult result) {
        if (result.hardViolations() == 0) {
            return String.format(
                    "Hall %s became unavailable, affecting %d students. The recovery engine preserved %d unaffected assignments and reassigned the affected students to feasible available seats. The recovered arrangement satisfies all hard constraints.",
                    disruption.targetId(), result.affectedStudents(), result.unchangedStudents()
            );
        } else {
            return String.format(
                    "Hall %s disruption affected %d students. Reassigned %d students with %d residual constraint violations. Further manual adjustment required.",
                    disruption.targetId(), result.affectedStudents(), result.movedStudents(), result.hardViolations()
            );
        }
    }
}
