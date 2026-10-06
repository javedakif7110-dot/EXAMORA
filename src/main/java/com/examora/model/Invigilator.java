package com.examora.model;

import java.util.Objects;

/**
 * Represents an Invigilator (faculty member assigned to supervise exam halls).
 */
public class Invigilator {
    private final String id;
    private final String name;
    private final String department;
    private int assignedDutyCount;
    private boolean available;

    public Invigilator(String id, String name, String department) {
        this.id = Objects.requireNonNull(id, "Invigilator ID cannot be null");
        this.name = Objects.requireNonNull(name, "Invigilator Name cannot be null");
        this.department = Objects.requireNonNull(department, "Department cannot be null");
        this.assignedDutyCount = 0;
        this.available = true;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public int getAssignedDutyCount() {
        return assignedDutyCount;
    }

    public void incrementDuty() {
        this.assignedDutyCount++;
    }

    public void decrementDuty() {
        if (this.assignedDutyCount > 0) this.assignedDutyCount--;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
