package com.water.service;

import com.water.model.SampleStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MeasurementLogicTest {

    private MeasurementService measurementService;
    private WaterSampleService waterSampleService;

    @BeforeEach
    void setUp() {
        measurementService = new MeasurementService();
        waterSampleService = new WaterSampleService();
    }

    // --- Тести MeasurementService ---

    @Test
    void calculateIntegralIndex_validFactors_computesWeightedIndexRounded() {
        double result = measurementService.calculateIntegralIndex(80.0, 60.0, 40.0);
        // 0.40 * 80 + 0.35 * 60 + 0.25 * 40 = 32 + 21 + 10 = 63.0
        assertEquals(63.0, result, 1e-9);
    }

    @Test
    void calculateIntegralIndex_fractionalResult_roundsToTwoDecimals() {
        double result = measurementService.calculateIntegralIndex(75.5, 82.3, 69.1);
        // 0.40*75.5 + 0.35*82.3 + 0.25*69.1 = 30.2 + 28.805 + 17.275 = 76.28
        assertEquals(76.28, result, 1e-9);
    }

    @Test
    void calculateIntegralIndex_boundaryValuesZeroAndHundred_succeeds() {
        assertEquals(0.0, measurementService.calculateIntegralIndex(0.0, 0.0, 0.0), 1e-9);
        assertEquals(100.0, measurementService.calculateIntegralIndex(100.0, 100.0, 100.0), 1e-9);
    }

    @Test
    void calculateIntegralIndex_factorBelowZero_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> measurementService.calculateIntegralIndex(-0.1, 50.0, 50.0));
    }

    @Test
    void calculateIntegralIndex_factorAboveHundred_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> measurementService.calculateIntegralIndex(50.0, 100.1, 50.0));
    }

    @Test
    void deleteMeasurement_nonPositiveId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> measurementService.deleteMeasurement(0));
        assertThrows(IllegalArgumentException.class, () -> measurementService.deleteMeasurement(-5));
    }

    // --- Тести WaterSampleService та SampleStatus ---

    @Test
    void changeSampleStatus_registeredToInAnalysis_succeeds() {
        SampleStatus next = waterSampleService.changeSampleStatus(
                SampleStatus.REGISTERED, SampleStatus.IN_ANALYSIS);
        assertEquals(SampleStatus.IN_ANALYSIS, next);
    }

    @Test
    void changeSampleStatus_registeredToArchived_succeeds() {
        SampleStatus next = waterSampleService.changeSampleStatus(
                SampleStatus.REGISTERED, SampleStatus.ARCHIVED);
        assertEquals(SampleStatus.ARCHIVED, next);
    }

    @Test
    void changeSampleStatus_inAnalysisToVerified_succeeds() {
        SampleStatus next = waterSampleService.changeSampleStatus(
                SampleStatus.IN_ANALYSIS, SampleStatus.VERIFIED);
        assertEquals(SampleStatus.VERIFIED, next);
    }

    @Test
    void changeSampleStatus_verifiedToArchived_succeeds() {
        SampleStatus next = waterSampleService.changeSampleStatus(
                SampleStatus.VERIFIED, SampleStatus.ARCHIVED);
        assertEquals(SampleStatus.ARCHIVED, next);
    }

    @Test
    void changeSampleStatus_archivedIsTerminalState_throwsIllegalStateException() {
        assertThrows(IllegalStateException.class,
                () -> waterSampleService.changeSampleStatus(SampleStatus.ARCHIVED, SampleStatus.REGISTERED));
        assertThrows(IllegalStateException.class,
                () -> waterSampleService.changeSampleStatus(SampleStatus.ARCHIVED, SampleStatus.IN_ANALYSIS));
    }

    @Test
    void changeSampleStatus_invalidJumpOrNullStatus_throwsException() {
        assertThrows(IllegalStateException.class,
                () -> waterSampleService.changeSampleStatus(SampleStatus.REGISTERED, SampleStatus.VERIFIED));
        assertThrows(IllegalArgumentException.class,
                () -> waterSampleService.changeSampleStatus(null, SampleStatus.IN_ANALYSIS));
        assertThrows(IllegalArgumentException.class,
                () -> waterSampleService.changeSampleStatus(SampleStatus.REGISTERED, null));
    }
}