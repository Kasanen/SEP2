package org.example.fuelConsCalc.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.example.fuelConsCalc.datasource.MariaDbConnection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @Test
    void calculateTotalCost_throwsForInvalidInput() {
        assertThrows(IllegalArgumentException.class,
                () -> FuelCalculatorService.calculateTotalCost(0.0, 1.95));
        assertThrows(IllegalArgumentException.class,
                () -> FuelCalculatorService.calculateTotalCost(10.0, -1.0));
    }

    @Test
    void saveCalculation_returnsTrue_whenInsertSucceeds() throws SQLException {
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<MariaDbConnection> mariaDbMock = org.mockito.Mockito.mockStatic(MariaDbConnection.class)) {
            mariaDbMock.when(MariaDbConnection::getConnection).thenReturn(connection);

            FuelCalculatorService service = new FuelCalculatorService();
            boolean saved = service.saveCalculation(120.0, 7.0, 2.0, 8.4, 16.8, "en");

            assertTrue(saved);
        }
    }

    @Test
    void saveCalculation_returnsFalse_whenNoRowsAffected() throws SQLException {
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(0);

        try (MockedStatic<MariaDbConnection> mariaDbMock = org.mockito.Mockito.mockStatic(MariaDbConnection.class)) {
            mariaDbMock.when(MariaDbConnection::getConnection).thenReturn(connection);

            FuelCalculatorService service = new FuelCalculatorService();
            boolean saved = service.saveCalculation(120.0, 7.0, 2.0, 8.4, 16.8, "en");

            assertFalse(saved);
        }
    }

    @Test
    void saveCalculation_returnsFalse_whenSQLExceptionOccurs() {
        try (MockedStatic<MariaDbConnection> mariaDbMock = org.mockito.Mockito.mockStatic(MariaDbConnection.class)) {
            mariaDbMock.when(MariaDbConnection::getConnection).thenThrow(new SQLException("DB unavailable"));

            FuelCalculatorService service = new FuelCalculatorService();
            boolean saved = service.saveCalculation(120.0, 7.0, 2.0, 8.4, 16.8, "en");

            assertFalse(saved);
        }
    }
}
