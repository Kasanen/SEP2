package org.example.fuelConsCalc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;

public class FuelConsumptionApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file
        URL fxmlUrl = getClass().getResource("/org/example/fuelConsCalc/fuelConsCalc-view.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlUrl);

        VBox root = loader.load();

        // Create scene first
        Scene scene = new Scene(root, 500, 600);

        // Then add CSS
        scene.getStylesheets().add(getClass().getResource("/org/example/fuelConsCalc/style.css").toExternalForm());

        primaryStage.setTitle("Fuel Consumption Calculator - LTR/RTL");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}