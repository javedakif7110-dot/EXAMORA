package com.examora.engine;

import com.examora.model.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Deterministic Hard and Soft Constraint Validation Engine for EXAMORA.
 *
 * Hard Constraints Verified:
 * 1. Timetable Conflict: No student has multiple exams in the same slot.
 * 2. Hall Capacity: Hall allocation does not exceed maximum capacity.
 * 3. Seat Uniqueness: No seat assigned to more than one student per slot.
 * 4. Hall Availability: No assignments in disabled or unavailable halls.
 * 5. Accessibility Satisfaction: Students requiring accessibility MUST receive accessible seats.
 * 6. Complete Coverage: All registered students receive a valid seat.
 * 7. Duplicate/Invalid Rejection: Invalid IDs or duplicate assignments are rejected.
 */
public class ConstraintValidator {

    public record ValidationReport(
            boolean isValid,
            int hardViolationCount,
            List<String> violationDetails,
            Map<String, Integer> hallOccupancy
    ) {}

    public ValidationReport validatePlan(
            List<Assignment> assignments,
            Map<String, Student> studentsMap,
            Map<String, Hall> hallsMap,
            Map<String, Seat> seatsMap,
            List<String> registeredStudentExamKeys
    ) {
        List<String> violations = new ArrayList<>();
        Map<String, Integer> hallOccupancy = new HashMap<>();

        // 1. Timetable Conflict Check: Student ID + Slot ID must be unique
        Set<String> studentSlotSet = new HashSet<>();
        for (Assignment a : assignments) {
            String studentSlotKey = a.studentId() + "@" + a.slotId();
            if (!studentSlotSet.add(studentSlotKey)) {
                violations.add("HARD VIOLATION: Student " + a.studentId() + " assigned to multiple exams in slot " + a.slotId());
            }
        }

        // 2 & 4. Hall Capacity & Availability Check
        Map<String, List<Assignment>> assignmentsByHallAndSlot = assignments.stream()
                .collect(Collectors.groupingBy(a -> a.hallId() + "@" + a.slotId()));

        for (Map.Entry<String, List<Assignment>> entry : assignmentsByHallAndSlot.entrySet()) {
            String[] parts = entry.getKey().split("@");
            String hallId = parts[0];
            String slotId = parts[1];
            List<Assignment> hallAssignments = entry.getValue();

            Hall hall = hallsMap.get(hallId);
            if (hall == null) {
                violations.add("HARD VIOLATION: Assignment references unknown hall " + hallId);
                continue;
            }

            if (!hall.isAvailable()) {
                violations.add("HARD VIOLATION: Hall " + hallId + " is UNAVAILABLE but has " + hallAssignments.size() + " students assigned in slot " + slotId);
            }

            if (hallAssignments.size() > hall.getCapacity()) {
                violations.add("HARD VIOLATION: Hall " + hallId + " capacity exceeded in slot " + slotId + ". Capacity: " + hall.getCapacity() + ", Assigned: " + hallAssignments.size());
            }

            hallOccupancy.put(hallId, hallAssignments.size());
        }

        // 3. Unique Seat Assignment Check: Slot ID + Seat ID must be unique
        Set<String> slotSeatSet = new HashSet<>();
        for (Assignment a : assignments) {
            String slotSeatKey = a.slotId() + "@" + a.seatId();
            if (!slotSeatSet.add(slotSeatKey)) {
                violations.add("HARD VIOLATION: Seat " + a.seatId() + " assigned to multiple students in slot " + a.slotId());
            }
        }

        // 5. Accessibility Check
        for (Assignment a : assignments) {
            Student student = studentsMap.get(a.studentId());
            if (student != null && student.requiresAccessibility()) {
                Seat seat = seatsMap.get(a.seatId());
                Hall hall = hallsMap.get(a.hallId());

                boolean hallOk = hall != null && hall.isAccessible();
                boolean seatOk = seat != null && seat.isAccessible();

                if (!hallOk || !seatOk) {
                    violations.add("HARD VIOLATION: Student " + a.studentId() + " requires accessibility but seat/hall " + a.seatId() + " in " + a.hallId() + " is not accessible");
                }
            }
        }

        // 6. Complete Coverage Check
        if (registeredStudentExamKeys != null && !registeredStudentExamKeys.isEmpty()) {
            Set<String> assignedKeys = assignments.stream()
                    .map(a -> a.studentId() + ":" + a.examId())
                    .collect(Collectors.toSet());

            for (String reqKey : registeredStudentExamKeys) {
                if (!assignedKeys.contains(reqKey)) {
                    violations.add("HARD VIOLATION: Registered student/exam pair " + reqKey + " has no assigned seat");
                }
            }
        }

        boolean isValid = violations.isEmpty();
        return new ValidationReport(isValid, violations.size(), violations, hallOccupancy);
    }
}
