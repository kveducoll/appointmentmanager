package cpe121.group3;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.control.Label;

import java.io.IOException;
import java.io.File;
import java.time.LocalTime;

public class MainMenuController {

    @FXML private Label greetLabel;

    @FXML
    private Button newButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button loadButton;

    @FXML
    private HBox titleBar;

    // Variables for window dragging
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private void initialize() {
        LocalTime localTime = LocalTime.now();
        int hour = localTime.getHour();
        String greeting;

        if (hour >= 5 && hour < 12) {
            greeting = "Good morning";
        } else if (hour >= 12 && hour < 18) {
            greeting = "Good afternoon";
        } else {
            greeting = "Good evening";
        }
        
        greetLabel.setText(greeting);

        setupWindowDragging();
    }

    private void setupWindowDragging() {
        // Set up drag functionality for the title bar
        titleBar.setOnMousePressed(this::handleMousePressed);
        titleBar.setOnMouseDragged(this::handleMouseDragged);
    }

    private void handleMousePressed(MouseEvent event) {
        // Record the initial click position
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    private void handleMouseDragged(MouseEvent event) {
        // Get the stage and move it based on mouse movement
        Stage stage = (Stage) titleBar.getScene().getWindow();
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }

    @FXML
    private void handleNewButton() {
        try {
            // Navigate to the table view
            App.setRoot("tableview");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load tableview.fxml");
        }
    }

    @FXML
    private void handleLoadButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Appointments");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Appointment Files", "*.apf")
        );

        File file = fileChooser.showOpenDialog(App.getPrimaryStage());
        if (file != null) {
            AppointmentManager appointmentManager = AppointmentManager.getInstance();
            if (appointmentManager.loadFromFile(file.getAbsolutePath())) {
                try {
                    // Navigate to the table view after successful load
                    App.setRoot("tableview");
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Failed to load tableview.fxml");
                }
            } else {
                System.err.println("Failed to load appointments from file: " + file.getName());
            }
        }
    }

    @FXML
    private void handleExitButton() {
        // Close the application
        Platform.exit();
    }
}
