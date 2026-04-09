package org.example.fuelConsCalc;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.example.fuelConsCalc.service.LocalizationService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;

import javafx.application.Platform;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

class FuelConsumptionControllerTest {

    private FuelConsumptionController controller;

    @BeforeAll
    static void initJavaFx() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
            latch.await(5, TimeUnit.SECONDS);
        } catch (IllegalStateException alreadyStarted) {
            // JavaFX runtime already initialized in this JVM.
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        controller = new FuelConsumptionController();
        runOnFxThreadAndWait(() -> {
            try {
                setField("rootVBox", new VBox());
                setField("lblTitle", new Label());
                setField("lblDistance", new Label());
                setField("lblFuel", new Label());
                setField("lblPrice", new Label());
                setField("txtDistance", new TextField());
                setField("txtFuel", new TextField());
                setField("txtPrice", new TextField());
                setField("btnCalculate", new Button());
                setField("lblResult", new Label());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void initialize_setsLocalizedTextsAndListeners() throws Exception {
        Map<String, String> strings = defaultStrings();

        try (MockedStatic<LocalizationService> localizationMock = mockStatic(LocalizationService.class)) {
            localizationMock.when(() -> LocalizationService.getLocalizedStrings(any(Locale.class))).thenReturn(strings);

            runOnFxThreadAndWait(() -> controller.initialize());

            Label title = (Label) getField("lblTitle");
            Button calculate = (Button) getField("btnCalculate");
            Label result = (Label) getField("lblResult");
            TextField distance = (TextField) getField("txtDistance");

            assertEquals("Consumption Calculator", title.getText());
            assertEquals("Calculate", calculate.getText());

            result.setText("previous");
            distance.setText("120");
            assertEquals("", result.getText());
        }
    }

    @Test
    void onCalculateClick_setsResultForValidInput() throws Exception {
        Map<String, String> strings = defaultStrings();

        try (MockedStatic<LocalizationService> localizationMock = mockStatic(LocalizationService.class)) {
            localizationMock.when(() -> LocalizationService.getLocalizedStrings(any(Locale.class))).thenReturn(strings);

            runOnFxThreadAndWait(() -> {
                controller.initialize();
                ((TextField) mustGetField("txtDistance")).setText("250");
                ((TextField) mustGetField("txtFuel")).setText("8");
                ((TextField) mustGetField("txtPrice")).setText("2");
                controller.onCalculateClick(null);
            });
        }

        Label result = (Label) getField("lblResult");
        assertTrue(result.getText().contains("20.00"));
        assertTrue(result.getText().contains("40.00"));
    }

    @Test
    void onCalculateClick_showsErrorForInvalidInput() throws Exception {
        Map<String, String> strings = defaultStrings();

        try (MockedStatic<LocalizationService> localizationMock = mockStatic(LocalizationService.class)) {
            localizationMock.when(() -> LocalizationService.getLocalizedStrings(any(Locale.class))).thenReturn(strings);

            runOnFxThreadAndWait(() -> {
                controller.initialize();
                ((TextField) mustGetField("txtDistance")).setText("abc");
                ((TextField) mustGetField("txtFuel")).setText("8");
                ((TextField) mustGetField("txtPrice")).setText("2");
                controller.onCalculateClick(null);
            });
        }

        Label result = (Label) getField("lblResult");
        assertEquals("Please enter valid numbers", result.getText());
    }

    @Test
    void languageButtons_toggleTextDirection() throws Exception {
        Map<String, String> strings = defaultStrings();

        try (MockedStatic<LocalizationService> localizationMock = mockStatic(LocalizationService.class)) {
            localizationMock.when(() -> LocalizationService.getLocalizedStrings(any(Locale.class))).thenReturn(strings);

            runOnFxThreadAndWait(() -> {
                controller.initialize();
                controller.onFAClick(null);
            });
            waitForFxEvents();

            VBox root = (VBox) getField("rootVBox");
            assertEquals(NodeOrientation.RIGHT_TO_LEFT, root.getNodeOrientation());

            runOnFxThreadAndWait(() -> controller.onENClick(null));
            waitForFxEvents();

            assertEquals(NodeOrientation.LEFT_TO_RIGHT, root.getNodeOrientation());
        }
    }

    private Map<String, String> defaultStrings() {
        Map<String, String> strings = new HashMap<>();
        strings.put("title", "Consumption Calculator");
        strings.put("lblDistance", "Distance (km)");
        strings.put("lblFuel", "Fuel Consumption (L/100 km)");
        strings.put("lblPrice", "Fuel price per liter");
        strings.put("distance", "Trip distance in kilometers");
        strings.put("fuel", "Fuel consumption (L/100 km)");
        strings.put("price", "Fuel price per liter");
        strings.put("calculate", "Calculate");
        strings.put("calc_result", "Total fuel needed: %.2f L | Total cost: %.2f");
        strings.put("error_invalid_input", "Please enter valid numbers");
        return strings;
    }

    private Object getField(String fieldName) throws Exception {
        Field field = FuelConsumptionController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(controller);
    }

    private Object mustGetField(String fieldName) {
        try {
            return getField(fieldName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setField(String fieldName, Object value) throws Exception {
        Field field = FuelConsumptionController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(controller, value);
    }

    private static void runOnFxThreadAndWait(Runnable runnable) throws InterruptedException {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
            return;
        }

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                runnable.run();
            } finally {
                latch.countDown();
            }
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new IllegalStateException("Timed out waiting for JavaFX task");
        }
    }

    private static void waitForFxEvents() throws InterruptedException {
        runOnFxThreadAndWait(() -> {
        });
    }
}
