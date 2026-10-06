package com.examora.model;

import java.util.Objects;

/**
 * Represents an Examination Hall at Chennai Institute of Technology.
 * Mutable state allows real-time status updates (e.g. Disruption -> ACTIVE / UNAVAILABLE).
 */
public class Hall {
    public enum HallStatus {
        ACTIVE,
        UNAVAILABLE,
        CAPACITY_REDUCED
    }

    private final String id;
    private final String name;
    private final String block;
    private int capacity;
    private final boolean isAccessible;
    private HallStatus status;

    public Hall(String id, String name, String block, int capacity, boolean isAccessible) {
        this.id = Objects.requireNonNull(id, "Hall ID cannot be null");
        this.name = Objects.requireNonNull(name, "Hall Name cannot be null");
        this.block = Objects.requireNonNull(block, "Block cannot be null");
        this.capacity = capacity;
        this.isAccessible = isAccessible;
        this.status = HallStatus.ACTIVE;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBlock() {
        return block;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isAccessible() {
        return isAccessible;
    }

    public HallStatus getStatus() {
        return status;
    }

    public void setStatus(HallStatus status) {
        this.status = status;
    }

    public boolean isAvailable() {
        return this.status == HallStatus.ACTIVE || this.status == HallStatus.CAPACITY_REDUCED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hall hall = (Hall) o;
        return Objects.equals(id, hall.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Hall{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", capacity=" + capacity +
                ", status=" + status +
                '}';
    }
}
