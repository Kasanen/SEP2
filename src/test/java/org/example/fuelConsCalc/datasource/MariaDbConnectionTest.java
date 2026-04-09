package org.example.fuelConsCalc.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mock;

class MariaDbConnectionTest {

    @Test
    void getConnection_returnsDriverManagerConnection() throws SQLException {
        Connection expected = mock(Connection.class);

        try (MockedStatic<DriverManager> driverManagerMock = org.mockito.Mockito.mockStatic(DriverManager.class)) {
            driverManagerMock.when(() -> DriverManager.getConnection(
                    org.mockito.ArgumentMatchers.any(),
                    org.mockito.ArgumentMatchers.any(),
                    org.mockito.ArgumentMatchers.any()))
                    .thenReturn(expected);

            Connection actual = MariaDbConnection.getConnection();
            assertSame(expected, actual);
        }
    }

    @Test
    void getConnection_throwsSQLException_whenDriverManagerFails() {
        SQLException expected = new SQLException("connection failed");
        try (MockedStatic<DriverManager> driverManagerMock = org.mockito.Mockito.mockStatic(DriverManager.class)) {
            driverManagerMock.when(() -> DriverManager.getConnection(
                    org.mockito.ArgumentMatchers.any(),
                    org.mockito.ArgumentMatchers.any(),
                    org.mockito.ArgumentMatchers.any()))
                    .thenThrow(expected);

            assertThrows(SQLException.class, MariaDbConnection::getConnection);
        }
    }
}
