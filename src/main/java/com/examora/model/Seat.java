package com.examora.model;

import java.util.Objects;

/**
 * Represents an individual seat within a hall.
 * Unique identifier is hallId + seatCode (e.g. H02-A01).
 */
public record Seat(
        String seatId,
        String hallId,
        String seatCode, // e.g. "A01", "F10"
        int rowNum,
        int colNum,
        boolean isAccessible
) {
    public Seat {
        Objects.requireNonNull(seatId, "Seat ID cannot be null");
        Objects.requireNonNull(hallId, "Hall ID cannot be null");
        Objects.requireNonNull(seatCode, "Seat Code cannot be null");
    }

    public static String generateSeatId(String hallId, String seatCode) {
        return hallId + "-" + seatCode;
    }
}
