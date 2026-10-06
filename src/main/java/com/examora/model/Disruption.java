package com.examora.model;

import java.util.Objects;

/**
 * Represents a simulated or real disruption event.
 */
public record Disruption(
        String disruptionId,
        DisruptionType type,
        String targetId, // Hall ID, Invigilator ID, or Seat ID
        int capacityReduction, // Used if type == HALL_CAPACITY_REDUCED
        String description
) {
    public enum DisruptionType {
        HALL_UNAVAILABLE,
        HALL_CAPACITY_REDUCED,
        INVIGILATOR_UNAVAILABLE,
        SEAT_BLOCK_UNAVAILABLE
    }

    public Disruption {
        Objects.requireNonNull(disruptionId, "Disruption ID cannot be null");
        Objects.requireNonNull(type, "Disruption Type cannot be null");
        Objects.requireNonNull(targetId, "Target ID cannot be null");
    }
}
