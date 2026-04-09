package org.example.fuelConsCalc.service;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.example.fuelConsCalc.datasource.MariaDbConnection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LocalizationServiceTest {

    @BeforeEach
    void clearCache() throws Exception {
        Field cacheField = LocalizationService.class.getDeclaredField("cache");
        cacheField.setAccessible(true);
        Map<?, ?> cache = (Map<?, ?>) cacheField.get(null);
        cache.clear();
    }

    @Test
    void loadStrings_returnsRowsFromDatabase() throws SQLException {
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(connection.prepareStatement("SELECT `key`, `value` FROM localization_strings WHERE language = ?"))
                .thenReturn(statement);
        when(statement.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, true, false);
        when(rs.getString("key")).thenReturn("title", "calculate");
        when(rs.getString("value")).thenReturn("Title", "Calc");

        try (MockedStatic<MariaDbConnection> mariaDbMock = org.mockito.Mockito.mockStatic(MariaDbConnection.class)) {
            mariaDbMock.when(MariaDbConnection::getConnection).thenReturn(connection);

            Map<String, String> result = LocalizationService.loadStrings("en");

            assertEquals("Title", result.get("title"));
            assertEquals("Calc", result.get("calculate"));
            assertEquals(2, result.size());
        }
    }

    @Test
    void loadStrings_returnsEmptyMapWhenSQLExceptionThrown() {
        try (MockedStatic<MariaDbConnection> mariaDbMock = org.mockito.Mockito.mockStatic(MariaDbConnection.class)) {
            mariaDbMock.when(MariaDbConnection::getConnection).thenThrow(new SQLException("DB down"));

            Map<String, String> result = LocalizationService.loadStrings("en");

            assertNotNull(result);
            assertEquals(0, result.size());
        }
    }

    @Test
    void getLocalizedStrings_cachesPerLanguage() {
        try (MockedStatic<LocalizationService> localizationMock = org.mockito.Mockito
                .mockStatic(LocalizationService.class, org.mockito.Mockito.CALLS_REAL_METHODS)) {
            Map<String, String> stub = new HashMap<>();
            stub.put("title", "Cached");

            localizationMock.when(() -> LocalizationService.loadStrings("en")).thenReturn(stub);

            Map<String, String> first = LocalizationService.getLocalizedStrings(Locale.of("en", "UK"));
            Map<String, String> second = LocalizationService.getLocalizedStrings(Locale.of("en", "US"));

            assertSame(first, second);
            localizationMock.verify(() -> LocalizationService.loadStrings("en"), org.mockito.Mockito.times(1));
        }
    }

    @Test
    void getLocalizedStrings_usesFallbackBundleWhenLoadFails() {
        try (MockedStatic<LocalizationService> localizationMock = org.mockito.Mockito
                .mockStatic(LocalizationService.class, org.mockito.Mockito.CALLS_REAL_METHODS)) {
            localizationMock.when(() -> LocalizationService.loadStrings("zz"))
                    .thenThrow(new RuntimeException("forced failure"));

            Map<String, String> result = LocalizationService.getLocalizedStrings(Locale.of("zz", "ZZ"));

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals("Please enter valid numbers", result.get("error_invalid_input"));
        }
    }

    @Test
    void getStrings_returnsValueWhenPresentInCache() throws Exception {
        Field cacheField = LocalizationService.class.getDeclaredField("cache");
        cacheField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, Map<String, String>> cache = (Map<String, Map<String, String>>) cacheField.get(null);

        Map<String, String> strings = new HashMap<>();
        strings.put("title", "Consumption Calculator");
        cache.put("en", strings);

        String value = LocalizationService.getStrings("en", "title");
        assertEquals("Consumption Calculator", value);
    }

    @Test
    void getStrings_returnsKeyWhenEntryMissing() {
        String value = LocalizationService.getStrings("en", "missing_key");
        assertEquals("missing_key", value);
    }
}
