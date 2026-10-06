package com.examora.engine;

import com.examora.model.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Deterministic Examination Planning & Initial Allocation Engine.
 * Generates initial conflict-free timetable, hall, seat, and invigilator allocations.
 */
public class AllocationEngine {

    public static class AllocationOutput {
        private final List<Assignment> assignments;
        private final double executionTimeMs;

        public AllocationOutput(List<Assignment> assignments, double executionTimeMs) {
            this.assignments = assignments;
            this.executionTimeMs = executionTimeMs;
        }

        public List<Assignment> getAssignments() {
            return assignments;
        }

        public double getExecutionTimeMs() {
            return executionTimeMs;
        }
    }

    public AllocationOutput generateInitialAllocation(
            List<Student> students,
            List<Exam> exams,
            List<ExamSlot> slots,
            List<Hall> halls,
            List<Seat> seats,
            List<Invigilator> invigilators
    ) {
        long startTime = System.nanoTime();
        List<Assignment> assignments = new ArrayList<>();

        Map<String, Hall> activeHallsMap = halls.stream()
                .filter(Hall::isAvailable)
                .collect(Collectors.toMap(Hall::getId, h -> h));

        Map<String, List<Seat>> seatsByHall = seats.stream()
                .filter(s -> activeHallsMap.containsKey(s.hallId()))
                .collect(Collectors.groupingBy(Seat::hallId));

        // Group students needing accessibility vs general
        List<Student> accessStudents = students.stream()
                .filter(Student::requiresAccessibility)
                .collect(Collectors.toList());

        List<Student> generalStudents = students.stream()
                .filter(s -> !s.requiresAccessibility())
                .collect(Collectors.toList());

        // Process exams across slots
        int slotIndex = 0;
        int invigilatorIndex = 0;

        for (Exam exam : exams) {
            ExamSlot currentSlot = slots.get(slotIndex % slots.size());

            // Get registered students for this exam
            List<Student> registered = new ArrayList<>();
            for (Student s : accessStudents) {
                if (s.registeredExamIds().contains(exam.id())) registered.add(s);
            }
            for (Student s : generalStudents) {
                if (s.registeredExamIds().contains(exam.id())) registered.add(s);
            }

            // Assign seats across active halls
            List<Hall> availableHallsList = new ArrayList<>(activeHallsMap.values());
            int currentHallIdx = 0;
            Map<String, Integer> hallSeatPointers = new HashMap<>();

            for (Student student : registered) {
                boolean assigned = false;
                // Try to find seat in current hall or round robin
                for (int attempts = 0; attempts < availableHallsList.size(); attempts++) {
                    Hall hall = availableHallsList.get((currentHallIdx + attempts) % availableHallsList.size());
                    List<Seat> hallSeats = seatsByHall.getOrDefault(hall.getId(), List.of());
                    int seatPtr = hallSeatPointers.getOrDefault(hall.getId(), 0);

                    // Filter accessibility match
                    if (student.requiresAccessibility() && !hall.isAccessible()) {
                        continue;
                    }

                    if (seatPtr < hallSeats.size() && seatPtr < hall.getCapacity()) {
                        Seat seat = hallSeats.get(seatPtr);

                        if (student.requiresAccessibility() && !seat.isAccessible()) {
                            // Find an accessible seat in this hall if available
                            Optional<Seat> accSeat = hallSeats.stream()
                                    .skip(seatPtr)
                                    .filter(Seat::isAccessible)
                                    .findFirst();
                            if (accSeat.isPresent()) {
                                seat = accSeat.get();
                            } else {
                                continue;
                            }
                        }

                        Invigilator inv = invigilators.isEmpty() ? null : invigilators.get(invigilatorIndex % invigilators.size());
                        if (inv != null) inv.incrementDuty();

                        String assignmentId = "ASN-" + (assignments.size() + 1);
                        assignments.add(new Assignment(
                                assignmentId,
                                student.id(),
                                exam.id(),
                                currentSlot.slotId(),
                                hall.getId(),
                                seat.seatId(),
                                inv != null ? inv.getId() : "INV-DEFAULT"
                        ));

                        hallSeatPointers.put(hall.getId(), seatPtr + 1);
                        assigned = true;
                        if ((seatPtr + 1) % 15 == 0) {
                            currentHallIdx = (currentHallIdx + 1) % availableHallsList.size();
                        }
                        break;
                    }
                }

                if (!assigned) {
                    System.err.println("Warning: Capacity reached or no suitable seat for student " + student.id());
                }
            }

            slotIndex++;
            invigilatorIndex++;
        }

        long endTime = System.nanoTime();
        double executionTimeMs = (endTime - startTime) / 1_000_000.0;
        return new AllocationOutput(assignments, executionTimeMs);
    }
}
