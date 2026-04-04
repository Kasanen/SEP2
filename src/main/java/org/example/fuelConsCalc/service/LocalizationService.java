package org.example.fuelConsCalc.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.example.fuelConsCalc.datasource.MariaDbConnection;

public class LocalizationService {

    private static final Map<String, Map<String, String>> cache = new HashMap<>();

    /**
     * Get localized strings for a specific locale
     */
    public static Map<String, String> getLocalizedStrings(Locale locale) {
        String language = locale.getLanguage();

        if (cache.containsKey(language)) {
            return cache.get(language);
        }

        try {
            Map<String, String> strings = loadStrings(language);
            cache.put(language, strings);
            return strings;
        } catch (Exception e) {
            System.err.println("Failed to load resource bundle for locale: " + locale);
            // Fallback to English
            Map<String, String> defaults = new HashMap<>();
            ResourceBundle fallback = ResourceBundle.getBundle(
                    "org.example.fuelConsCalc.i18n.MessagesBundle",
                    Locale.of("en", "UK"));
            for (String key : fallback.keySet()) {
                defaults.put(key, fallback.getString(key));
            }
            cache.put(language, defaults);
            // Use hardcoded defaults as last resort
            // defaults.put("title", "Consumption Calculator");
            // defaults.put("distance", "Distance (km):");
            // defaults.put("fuel", "Fuel (L):");
            // defaults.put("calculate", "Calculate consumption");
            // defaults.put("calc_result", "Consumption: %.1f - %s");
            // defaults.put("error_invalid_input", "Please enter valid numbers");
            return defaults;
        }
    }

    public static Map<String, String> loadStrings(String language) {
        Map<String, String> strings = new HashMap<>();

        String sql = "SELECT `key`, `value` FROM localization_strings WHERE language = ?";
        try (Connection conn = MariaDbConnection.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, language);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                strings.put(rs.getString("key"), rs.getString("value"));
            }
            System.out.println("Loaded " + strings.size() + " strings for language: " + language);
        } catch (SQLException e) {
            System.out.println("Failed to load strings for: " + language);
            e.printStackTrace();
        }
        return strings;
    }

    public static String getStrings(String language, String key) {
        Map<String, String> strings = cache.getOrDefault(language, new HashMap<>());
        return strings.getOrDefault(key, key);
    }
}
