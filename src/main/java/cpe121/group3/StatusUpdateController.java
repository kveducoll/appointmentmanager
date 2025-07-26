package cpe121.group3;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class StatusUpdateController implements Initializable {

    @FXML private VBox titleBar;
    @FXML private Label formTitle;
    @FXML private Label appointmentInfoLabel;
    @FXML private Label currentStatusLabel;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Button updateButton;
    @FXML private Button cancelButton;

    private static Appointment selectedAppointment = null;
    private Stage popupStage;
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        // Initialize Status options
        statusComboBox.getItems().addAll(
            "Scheduled", "Confirmed", "In Progress", "Completed", "Cancelled", "No Show"
        );

        // Populate fields if appointment is selected
        if (selectedAppointment != null) {
            populateFormWithAppointment(selectedAppointment);
        }
    }

    public static void setSelectedAppointment(Appointment appointment) {
        selectedAppointment = appointment;
    }

    public void setPopupStage(Stage stage) {
        this.popupStage = stage;
    }

    private void populateFormWithAppointment(Appointment appointment) {
        // Display appointment info
        String appointmentInfo = appointment.getTitle() + " - " + appointment.getParticipant();
        if (appointmentInfo.length() > 40) {
            appointmentInfo = appointmentInfo.substring(0, 37) + "...";
        }
        appointmentInfoLabel.setText(appointmentInfo);
        
        // Display current status
        currentStatusLabel.setText(appointment.getStatus());
        
        // Set current status as default selection
        statusComboBox.setValue(appointment.getStatus());
    }

    @FXML
    private void updateStatus() {
        if (selectedAppointment == null) {
            showAlert("Error", "No appointment selected.");
            return;
        }

        String newStatus = statusComboBox.getValue();
        if (newStatus == null || newStatus.trim().isEmpty()) {
            showAlert("Validation Error", "Please select a status.");
            return;
        }

        // Check if status actually changed
        if (newStatus.equals(selectedAppointment.getStatus())) {
            showAlert("No Changes", "The status is already set to '" + newStatus + "'.");
            return;
        }

        // Update the appointment status
        selectedAppointment.setStatus(newStatus);
        
        showAlert("Success", "Status updated successfully to '" + newStatus + "'");
        
        // Clear selection and close popup
        selectedAppointment = null;
        if (popupStage != null) {
            popupStage.close();
        }
    }

    @FXML
    private void closePopup() {
        selectedAppointment = null;
        if (popupStage != null) {
            popupStage.close();
        }
    }

    // Custom title bar drag functionality
    @FXML
    private void onTitleBarPressed(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    @FXML
    private void onTitleBarDragged(MouseEvent event) {
        if (popupStage != null) {
            popupStage.setX(event.getScreenX() - xOffset);
            popupStage.setY(event.getScreenY() - yOffset);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
