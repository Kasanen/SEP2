package org.example.fuelConsCalc.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.example.fuelConsCalc.datasource.MariaDbConnection;

public final class FuelCalculatorService {

    public FuelCalculatorService() {
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

    private static final String INSERT = "INSERT INTO calculator_mem "
            + "(distance, fuel, price, total_fuel, total_cost, language) " + "VALUES (?, ?, ?, ?, ?, ?)";

    public boolean saveCalculation(double distance, double fuel, double price, double totalFuel, double totalCost,
            String language) {
        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(INSERT)) {
            statement.setDouble(1, distance);
            statement.setDouble(2, fuel);
            statement.setDouble(3, price);
            statement.setDouble(4, totalFuel);
            statement.setDouble(5, totalCost);
            statement.setString(6, language);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saving calculation: " + e.getMessage());
            return false;
        }
    }

    public Connection getConnection() throws SQLException {
        return MariaDbConnection.getConnection();
    }
}
