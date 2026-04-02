package org.example.fuelConsCalc.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FuelCalculatorServiceTest {

    @Test
    void calculateTotalFuelLiters_returnsExpectedValue() {
        double totalFuel = FuelCalculatorService.calculateTotalFuelLiters(250.0, 8.0);
        assertEquals(20.0, totalFuel, 1e-9);
    }

    @Test
    void calculateTotalCost_returnsExpectedValue() {
        double totalCost = FuelCalculatorService.calculateTotalCost(20.0, 1.95);
        assertEquals(39.0, totalCost, 1e-9);
    }

    @Test
    void calculateTotalFuelLiters_throwsForInvalidInput() {
        assertThrows(IllegalArgumentException.class,
                () -> FuelCalculatorService.calculateTotalFuelLiters(0.0, 8.0));
        assertThrows(IllegalArgumentException.class,
                () -> FuelCalculatorService.calculateTotalFuelLiters(250.0, -1.0));
    }
}
