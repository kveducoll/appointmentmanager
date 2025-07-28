package cpe121.group3;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.control.Label;

import java.io.IOException;
import java.io.File;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class MainMenuController {

    @FXML private Label greetLabel;
    @FXML private Label dropLabel;
    @FXML private BorderPane dropArea;

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
        setupDragAndDrop();
    }

    private void setupWindowDragging() {
        // Set up drag functionality for the title bar
        titleBar.setOnMousePressed(this::handleMousePressed);
        titleBar.setOnMouseDragged(this::handleMouseDragged);
    }

    private void setupDragAndDrop() {
        // Set up drag and drop functionality for the drop area
        dropArea.setOnDragOver(this::handleDragOver);
        dropArea.setOnDragDropped(this::handleDragDropped);
        dropArea.setOnDragEntered(this::handleDragEntered);
        dropArea.setOnDragExited(this::handleDragExited);
    }

    private void handleDragOver(DragEvent event) {
        if (event.getGestureSource() != dropArea && event.getDragboard().hasFiles()) {
            // Check if the dragged file has .apf extension
            List<File> files = event.getDragboard().getFiles();
            if (!files.isEmpty() && files.get(0).getName().toLowerCase().endsWith(".apf")) {
                event.acceptTransferModes(TransferMode.COPY);
            }
        }
        event.consume();
    }

    private void handleDragDropped(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        boolean success = false;

        if (dragboard.hasFiles()) {
            List<File> files = dragboard.getFiles();
            if (!files.isEmpty()) {
                File file = files.get(0);
                if (file.getName().toLowerCase().endsWith(".apf")) {
                    AppointmentManager manager = AppointmentManager.getInstance();
                    if (manager.hasUnsavedChanges()) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Unsaved Changes");
                        alert.setHeaderText("You have unsaved changes.");
                        alert.setContentText("Do you want to continue and lose unsaved changes?");
                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() != ButtonType.OK) {
                            event.setDropCompleted(false);
                            event.consume();
                            resetDropAreaAppearance();
                            return;
                        }
                    }
                    success = loadAppointmentFileWithDialog(file);
                }
            }
        }

        event.setDropCompleted(success);
        event.consume();
        // Reset the drop area appearance
        resetDropAreaAppearance();
    }

    private void handleDragEntered(DragEvent event) {
        if (event.getGestureSource() != dropArea && event.getDragboard().hasFiles()) {
            List<File> files = event.getDragboard().getFiles();
            if (!files.isEmpty() && files.get(0).getName().toLowerCase().endsWith(".apf")) {
                // Change appearance to indicate valid drop target
                dropArea.setStyle("-fx-background-color: #2a4d3a; -fx-background-radius: 5; -fx-border-color: #09ab72; -fx-border-width: 2px; -fx-border-radius: 5;");
                dropLabel.setText("Release to load file");
                dropLabel.setTextFill(javafx.scene.paint.Color.web("#09ab72"));
            } else {
                // Invalid file type
                dropArea.setStyle("-fx-background-color: #4d2a2a; -fx-background-radius: 5; -fx-border-color: #ab0909; -fx-border-width: 2px; -fx-border-radius: 5;");
                dropLabel.setText("Only .apf files allowed");
                dropLabel.setTextFill(javafx.scene.paint.Color.web("#fdfdfdff"));
            }
        }
        event.consume();
    }

    private void handleDragExited(DragEvent event) {
        resetDropAreaAppearance();
        event.consume();
    }

    private void resetDropAreaAppearance() {
        dropArea.setStyle("-fx-background-color: #252525; -fx-background-radius: 5;");
        dropLabel.setText("Drop .apf here");
        dropLabel.setTextFill(javafx.scene.paint.Color.web("#686868"));
    }

    private boolean loadAppointmentFileWithDialog(File file) {
        AppointmentManager appointmentManager = AppointmentManager.getInstance();
        if (appointmentManager.loadFromFile(file.getAbsolutePath())) {
            try {
                // Navigate to the table view after successful load
                App.setRoot("tableview");
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                showErrorDialog("Failed to load tableview.fxml");
                return false;
            }
        } else {
            showErrorDialog("Failed to load appointments from file: " + file.getName());
            return false;
        }
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("File Load Error");
        alert.setContentText(message);
        alert.showAndWait();
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
    private void handleCreateNewButton() {
        AppointmentManager manager = AppointmentManager.getInstance();

        if (manager.hasUnsavedChanges()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Unsaved Changes");
            alert.setHeaderText("You have unsaved changes.");
            alert.setContentText("Do you want to continue and lose unsaved changes?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() != ButtonType.OK) {
                return;
            }
        }
        manager.clearAllAppointments(); // or manager.clearAllAppointments() if that's the correct method
        try {
            App.setRoot("tableview");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e);
        }
}

    @FXML
    private void handleOpenButton() {
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
            AppointmentManager manager = AppointmentManager.getInstance();
            if (manager.hasUnsavedChanges()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Unsaved Changes");
                alert.setHeaderText("You have unsaved changes.");
                alert.setContentText("Do you want to continue and lose unsaved changes?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() != ButtonType.OK) {
                    return;
                }
            }
            loadAppointmentFileWithDialog(file);
        }
    }

    @FXML
    private void handleExitButton() {
        // Close the application
        Platform.exit();
    }

    @FXML
    private void showAboutPopup() throws IOException {
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("about.fxml"));
        javafx.scene.Parent root = loader.load();
        javafx.stage.Stage popupStage = new javafx.stage.Stage();
        popupStage.initStyle(javafx.stage.StageStyle.UNDECORATED);
        popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        popupStage.initOwner(App.getPrimaryStage());
        javafx.scene.Scene scene = new javafx.scene.Scene(root, 604, 612);
        popupStage.setScene(scene);
        popupStage.setTitle("About");
        popupStage.setResizable(false);
        popupStage.showAndWait();
    }

    // Window control methods
    @FXML
    private void onTitleBarPressed(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    @FXML
    private void onTitleBarDragged(MouseEvent event) {
        Stage stage = (Stage) titleBar.getScene().getWindow();
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }

    @FXML
    private void minimizeWindow() {
        Stage stage = (Stage) titleBar.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void maximizeWindow() {
        Stage stage = (Stage) titleBar.getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }

    @FXML
    private void closeWindow() {
        Platform.exit();
    }
}
