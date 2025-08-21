package cpe121.group3;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.Cursor;
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
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class MainMenuController {
    @FXML private Label greetLabel;
    @FXML private Label dropLabel;
    @FXML private BorderPane dropArea;
    @FXML private VBox recentFilesContainer;
    @FXML private VBox recentFilesList;
    @FXML private Button clearRecentButton;

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

    // Variables for window resizing
    private boolean isResizing = false;
    private double resizeMargin = 10; 
    private String resizeDirection = "";

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
        setupWindowResizing();
        loadRecentFiles();
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

    private void setupWindowResizing() {
        // Wait for scene to be fully loaded
        Platform.runLater(() -> {
            if (titleBar.getScene() != null) {
                titleBar.getScene().getRoot().setOnMouseMoved(this::handleMouseMoved);
                titleBar.getScene().getRoot().setOnMousePressed(this::handleResizeMousePressed);
                titleBar.getScene().getRoot().setOnMouseDragged(this::handleResizeMouseDragged);
                titleBar.getScene().getRoot().setOnMouseReleased(this::handleResizeMouseReleased);
            }
        });
    }

    private void handleMouseMoved(MouseEvent event) {
        if (isResizing) return;
        
        Stage stage = (Stage) titleBar.getScene().getWindow();
        if (stage.isMaximized()) return;
        
        double sceneWidth = titleBar.getScene().getWidth();
        double sceneHeight = titleBar.getScene().getHeight();
        double mouseX = event.getSceneX();
        double mouseY = event.getSceneY();
        
        // cursor and resize direction
        boolean atLeft = mouseX < resizeMargin;
        boolean atRight = mouseX > sceneWidth - resizeMargin;
        boolean atTop = mouseY < resizeMargin;
        boolean atBottom = mouseY > sceneHeight - resizeMargin;
        
        if (atLeft && atTop) {
            titleBar.getScene().setCursor(Cursor.NW_RESIZE);
            resizeDirection = "NW";
        } else if (atRight && atTop) {
            titleBar.getScene().setCursor(Cursor.NE_RESIZE);
            resizeDirection = "NE";
        } else if (atLeft && atBottom) {
            titleBar.getScene().setCursor(Cursor.SW_RESIZE);
            resizeDirection = "SW";
        } else if (atRight && atBottom) {
            titleBar.getScene().setCursor(Cursor.SE_RESIZE);
            resizeDirection = "SE";
        } else if (atLeft) {
            titleBar.getScene().setCursor(Cursor.W_RESIZE);
            resizeDirection = "W";
        } else if (atRight) {
            titleBar.getScene().setCursor(Cursor.E_RESIZE);
            resizeDirection = "E";
        } else if (atTop) {
            titleBar.getScene().setCursor(Cursor.N_RESIZE);
            resizeDirection = "N";
        } else if (atBottom) {
            titleBar.getScene().setCursor(Cursor.S_RESIZE);
            resizeDirection = "S";
        } else {
            titleBar.getScene().setCursor(Cursor.DEFAULT);
            resizeDirection = "";
        }
    }

    private void handleResizeMousePressed(MouseEvent event) {
        if (!resizeDirection.isEmpty()) {
            isResizing = true;
            xOffset = event.getScreenX();
            yOffset = event.getScreenY();
        }
    }

    private void handleResizeMouseDragged(MouseEvent event) {
        if (!isResizing) return;
        
        Stage stage = (Stage) titleBar.getScene().getWindow();
        if (stage.isMaximized()) return;
        
        double deltaX = event.getScreenX() - xOffset;
        double deltaY = event.getScreenY() - yOffset;
        
        double newWidth = stage.getWidth();
        double newHeight = stage.getHeight();
        double newX = stage.getX();
        double newY = stage.getY();
        
        switch (resizeDirection) {
            case "E":
                newWidth = stage.getWidth() + deltaX;
                break;
            case "W":
                newWidth = stage.getWidth() - deltaX;
                newX = stage.getX() + deltaX;
                break;
            case "S":
                newHeight = stage.getHeight() + deltaY;
                break;
            case "N":
                newHeight = stage.getHeight() - deltaY;
                newY = stage.getY() + deltaY;
                break;
            case "SE":
                newWidth = stage.getWidth() + deltaX;
                newHeight = stage.getHeight() + deltaY;
                break;
            case "SW":
                newWidth = stage.getWidth() - deltaX;
                newHeight = stage.getHeight() + deltaY;
                newX = stage.getX() + deltaX;
                break;
            case "NE":
                newWidth = stage.getWidth() + deltaX;
                newHeight = stage.getHeight() - deltaY;
                newY = stage.getY() + deltaY;
                break;
            case "NW":
                newWidth = stage.getWidth() - deltaX;
                newHeight = stage.getHeight() - deltaY;
                newX = stage.getX() + deltaX;
                newY = stage.getY() + deltaY;
                break;
        }
        
        // Apply minimum size constraints
        if (newWidth >= stage.getMinWidth()) {
            stage.setWidth(newWidth);
            if (resizeDirection.contains("W")) {
                stage.setX(newX);
            }
        }
        
        if (newHeight >= stage.getMinHeight()) {
            stage.setHeight(newHeight);
            if (resizeDirection.contains("N")) {
                stage.setY(newY);
            }
        }
        
        xOffset = event.getScreenX();
        yOffset = event.getScreenY();
    }

    private void handleResizeMouseReleased(MouseEvent event) {
        isResizing = false;
        titleBar.getScene().setCursor(Cursor.DEFAULT);
        resizeDirection = "";
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
        // Only handle dragging if we're not in a resize area and not resizing
        if (resizeDirection.isEmpty() && !isResizing) {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        }
    }

    private void handleMouseDragged(MouseEvent event) {
        // Only drag window if we're not resizing and not in a resize area
        if (resizeDirection.isEmpty() && !isResizing) {
            Stage stage = (Stage) titleBar.getScene().getWindow();
            if (!stage.isMaximized()) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        }
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
    private void onTitleBarClicked(MouseEvent event) {
        if (event.getClickCount() == 2) {
            maximizeWindow();
        }
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

    private void loadRecentFiles() {
        AppointmentManager manager = AppointmentManager.getInstance();
        List<String> recentFiles = manager.getRecentFiles();
        
        recentFilesList.getChildren().clear();
        
        if (recentFiles.isEmpty()) {
            recentFilesContainer.setVisible(false);
            return;
        }
        
        recentFilesContainer.setVisible(true);
        
        for (String filePath : recentFiles) {
            File file = new File(filePath);
            
            // Create main container VBox for each recent file (cleaner without HBox)
            VBox fileContainer = new VBox();
            fileContainer.setAlignment(Pos.CENTER_LEFT);
            fileContainer.setMaxWidth(Double.MAX_VALUE);
            fileContainer.setPadding(new Insets(8, 15, 8, 15));
            fileContainer.setSpacing(2);
            fileContainer.setStyle("-fx-background-color: transparent;");
            
            // Filename label
            Label filenameLabel = new Label(file.getName());
            filenameLabel.setStyle("-fx-text-fill: #09ab72; -fx-font-size: 13px; -fx-font-weight: bold;");
            
            // Full path label (grey)
            Label pathLabel = new Label(filePath);
            pathLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 10px;");
            
            fileContainer.getChildren().addAll(filenameLabel, pathLabel);
            
            // Create context menu for right-click
            ContextMenu contextMenu = new ContextMenu();
            contextMenu.setStyle(
                "-fx-background-color: #2d2d2d; " +
                "-fx-border-color: #555555; " +
                "-fx-border-width: 1px; " +
                "-fx-background-radius: 5px; " +
                "-fx-border-radius: 5px; " +
                "-fx-padding: 3px;"
            );
            
            // Open menu item
            MenuItem openItem = new MenuItem("Open");
            openItem.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 8px 15px; " +
                "-fx-font-size: 12px; " +
                "-fx-background-radius: 3px;"
            );
            openItem.setOnAction(e -> {
                if (file.exists()) {
                    AppointmentManager appointmentManager = AppointmentManager.getInstance();
                    if (appointmentManager.hasUnsavedChanges()) {
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
                } else {
                    showErrorDialog("File no longer exists: " + file.getName());
                    loadRecentFiles(); // Refresh the list
                }
            });
            
            // Remove menu item
            MenuItem removeItem = new MenuItem("Remove from list");
            removeItem.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 8px 15px; " +
                "-fx-font-size: 12px; " +
                "-fx-background-radius: 3px;"
            );
            
            removeItem.setOnAction(e -> {
                manager.removeFromRecentFiles(filePath);
                loadRecentFiles(); // Refresh the list
            });
            
            contextMenu.getItems().addAll(openItem, removeItem);
            
            // Add hover effects
            fileContainer.setOnMouseEntered(e -> fileContainer.setStyle("-fx-background-color: #2a4d3a; -fx-background-radius: 5px;"));
            fileContainer.setOnMouseExited(e -> fileContainer.setStyle("-fx-background-color: transparent;"));
            
            // Handle mouse clicks (both left and right)
            fileContainer.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY) { // Left click
                    if (file.exists()) {
                        AppointmentManager appointmentManager = AppointmentManager.getInstance();
                        if (appointmentManager.hasUnsavedChanges()) {
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
                    } else {
                        showErrorDialog("File no longer exists: " + file.getName());
                        loadRecentFiles(); // Refresh the list
                    }
                } else if (e.getButton() == MouseButton.SECONDARY) { // Right click
                    contextMenu.show(fileContainer, e.getScreenX(), e.getScreenY());
                }
            });
            
            // Add cursor pointer
            fileContainer.setCursor(Cursor.HAND);
            
            recentFilesList.getChildren().add(fileContainer);
        }
    }
    
    @FXML
    private void clearRecentFiles() {
        AppointmentManager manager = AppointmentManager.getInstance();
        manager.clearRecentFiles();
        loadRecentFiles();
    }
}
