package org.example.fuelConsCalc.service;

public final class FuelCalculatorService {

    private FuelCalculatorService() {
    }

    public static double calculateTotalFuelLiters(double distanceKm, double fuelConsumptionPer100Km) {
        if (distanceKm <= 0 || fuelConsumptionPer100Km <= 0) {
            throw new IllegalArgumentException("Distance and fuel consumption must be greater than 0");
        }
        return (distanceKm * fuelConsumptionPer100Km) / 100.0;
    }

    public static double calculateTotalCost(double totalFuelLiters, double pricePerLiter) {
        if (totalFuelLiters <= 0 || pricePerLiter <= 0) {
            throw new IllegalArgumentException("Fuel and price must be greater than 0");
        }
        return totalFuelLiters * pricePerLiter;
    }
}
