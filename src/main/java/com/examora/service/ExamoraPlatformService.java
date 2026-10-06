package com.examora.service;

import com.examora.ai.AIExplanationService;
import com.examora.data.ExamoraDataLoader;
import com.examora.engine.AllocationEngine;
import com.examora.engine.ConstraintValidator;
import com.examora.engine.TwoPassRecoveryEngine;
import com.examora.model.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Core Orchestrator for EXAMORA Platform.
 *
 * Implements full lifecycle workflow:
 * PLAN -> VERIFY -> SIMULATE -> DISRUPT -> RECOVER -> VERIFY AGAIN
 */
public class ExamoraPlatformService {

    private ExamoraDataLoader.DatasetBundle currentDataset;
    private List<Assignment> currentAssignments = new ArrayList<>();
    private AllocationEngine.AllocationOutput lastAllocationOutput;
    private RecoveryResult lastRecoveryResult;

    private final AllocationEngine allocationEngine = new AllocationEngine();
    private final ConstraintValidator validator = new ConstraintValidator();
    private final TwoPassRecoveryEngine recoveryEngine = new TwoPassRecoveryEngine();
    private final AIExplanationService aiService = new AIExplanationService();

    private boolean isBenchmarkDataset = true;

    public ExamoraPlatformService() {
        loadDataset(true); // Default to genuine 180-student benchmark prototype dataset
    }

    public void loadDataset(boolean benchmarkMode) {
        this.isBenchmarkDataset = benchmarkMode;
        if (benchmarkMode) {
            this.currentDataset = ExamoraDataLoader.loadBenchmarkDataset();
        } else {
            this.currentDataset = ExamoraDataLoader.loadLargeSyntheticDataset();
        }
        generateInitialPlan();
    }

    public AllocationEngine.AllocationOutput generateInitialPlan() {
        // Reset hall statuses to ACTIVE
        for (Hall h : currentDataset.halls()) {
            h.setStatus(Hall.HallStatus.ACTIVE);
        }

        this.lastAllocationOutput = allocationEngine.generateInitialAllocation(
                currentDataset.students(),
                currentDataset.exams(),
                currentDataset.slots(),
                currentDataset.halls(),
                currentDataset.seats(),
                currentDataset.invigilators()
        );

        this.currentAssignments = new ArrayList<>(lastAllocationOutput.getAssignments());
        this.lastRecoveryResult = null;
        return lastAllocationOutput;
    }

    public ConstraintValidator.ValidationReport validateCurrentPlan() {
        Map<String, Student> studentMap = currentDataset.students().stream()
                .collect(Collectors.toMap(Student::id, s -> s));
        Map<String, Hall> hallMap = currentDataset.halls().stream()
                .collect(Collectors.toMap(Hall::getId, h -> h));
        Map<String, Seat> seatMap = currentDataset.seats().stream()
                .collect(Collectors.toMap(Seat::seatId, s -> s));

        return validator.validatePlan(currentAssignments, studentMap, hallMap, seatMap, null);
    }

    public RecoveryResult simulateDisruptionAndRecover(String hallId, Disruption.DisruptionType disruptionType) {
        Disruption disruption = new Disruption(
                "DIS-" + System.currentTimeMillis(),
                disruptionType,
                hallId,
                0,
                "Simulated disruption for Hall " + hallId
        );

        double initialTime = (lastAllocationOutput != null) ? lastAllocationOutput.getExecutionTimeMs() : 7.205;

        this.lastRecoveryResult = recoveryEngine.recoverFromDisruption(
                disruption,
                currentAssignments,
                currentDataset.students(),
                currentDataset.halls(),
                currentDataset.seats(),
                initialTime
        );

        // Update current active assignments to recovered plan
        this.currentAssignments = new ArrayList<>(this.lastRecoveryResult.recoveredAssignments());

        return this.lastRecoveryResult;
    }

    // Getters for controller / web dashboard
    public ExamoraDataLoader.DatasetBundle getCurrentDataset() {
        return currentDataset;
    }

    public List<Assignment> getCurrentAssignments() {
        return currentAssignments;
    }

    public RecoveryResult getLastRecoveryResult() {
        return lastRecoveryResult;
    }

    public AllocationEngine.AllocationOutput getLastAllocationOutput() {
        return lastAllocationOutput;
    }

    public boolean isBenchmarkDataset() {
        return isBenchmarkDataset;
    }

    public String getAIExplanationForRecovery() {
        if (lastRecoveryResult == null) {
            return "No disruption simulation active. All examination assignments are operating normally.";
        }
        return lastRecoveryResult.aiExplanation();
    }
}
