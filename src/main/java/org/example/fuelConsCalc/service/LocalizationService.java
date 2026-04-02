package org.example.fuelConsCalc.service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class LocalizationService {
    /**
     * Get localized strings for a specific locale
     */
    public static Map<String, String> getLocalizedStrings(Locale locale) {
        Map<String, String> strings = new HashMap<>();

        try {
            ResourceBundle bundle = ResourceBundle.getBundle(
                    "org.example.fuelConsCalc.i18n.MessagesBundle",
                    locale
            );

            // Extract all keys
            for (String key : bundle.keySet()) {
                strings.put(key, bundle.getString(key));
            }
        } catch (Exception e) {
            System.err.println("Failed to load resource bundle for locale: " + locale);
            // Fallback to English
            try {
                ResourceBundle fallback = ResourceBundle.getBundle(
                        "org.example.fuelConsCalc.i18n.MessagesBundle",
                    Locale.of("en", "UK")
                );
                for (String key : fallback.keySet()) {
                    strings.put(key, fallback.getString(key));
                }
            } catch (Exception ex) {
                // Use hardcoded defaults as last resort
                strings.put("title", "Consumption Calculator");
                strings.put("distance", "Distance (km):");
                strings.put("fuel", "Fuel (L):");
                strings.put("calculate", "Calculate consumption");
                strings.put("calc_result", "Consumption: %.1f - %s");
                strings.put("error_invalid_input", "Please enter valid numbers");
            }
        }

        return strings;
    }


}
