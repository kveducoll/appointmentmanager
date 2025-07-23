package cpe121.group4;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SecondaryController implements Initializable {

    @FXML private Label formTitle;
    @FXML private TextField titleField;
    @FXML private TextField participantField;
    @FXML private DatePicker appointmentDatePicker;
    @FXML private TextField appointmentTimeField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<String> statusComboBox;

    private AppointmentManager appointmentManager;
    private static Appointment editingAppointment = null;
    private boolean isEditMode = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        appointmentManager = AppointmentManager.getInstance();
        
        // Initialize status combo box
        statusComboBox.getItems().addAll(
            "Scheduled", "Confirmed", "In Progress", "Completed", "Cancelled", "No Show"
        );
        statusComboBox.setValue("Scheduled");

        // Check if Existing Appointment
        if (editingAppointment != null) {
            isEditMode = true;
            formTitle.setText("Edit Appointment");
            populateFormWithAppointment(editingAppointment);
        } else {
            isEditMode = false;
            formTitle.setText("Add New Appointment");
            clearForm();
        }

        // Set up time field validation
        appointmentTimeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,2}:?\\d{0,2}")) {
                appointmentTimeField.setText(oldValue);
            }
        });
    }

    @FXML
    private void saveAppointment() throws IOException {
        if (validateForm()) {
            String title = titleField.getText().trim();
            String participant = participantField.getText().trim();
            String appointmentDate = appointmentDatePicker.getValue() != null ? 
                appointmentDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";
            String appointmentTime = appointmentTimeField.getText().trim();
            String description = descriptionArea.getText().trim();
            String status = statusComboBox.getValue();

            if (isEditMode && editingAppointment != null) {
                // Update existing appointment
                editingAppointment.setTitle(title);
                editingAppointment.setParticipant(participant);
                editingAppointment.setAppointmentDate(appointmentDate);
                editingAppointment.setAppointmentTime(appointmentTime);
                editingAppointment.setDescription(description);
                editingAppointment.setStatus(status);
                
                showAlert("Success", "Appointment updated successfully!");
            } else {
                // Create new appointment
                Appointment newAppointment = new Appointment(
                    title, participant, appointmentDate, appointmentTime, description, status
                );
                appointmentManager.addAppointment(newAppointment);
                showAlert("Success", "Appointment added successfully!");
            }

            // Clear editing tas balik sa main
            editingAppointment = null;
            App.setRoot("primary");
        }
    }

    @FXML
    private void clearForm() {
        titleField.clear();
        participantField.clear();
        appointmentDatePicker.setValue(null);
        appointmentTimeField.clear();
        descriptionArea.clear();
        statusComboBox.setValue("Scheduled");
    }

    @FXML
    private void switchToPrimary() throws IOException {
        editingAppointment = null;
        App.setRoot("primary");
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (titleField.getText().trim().isEmpty()) {
            errors.append("- Title is required\n");
        }

        if (participantField.getText().trim().isEmpty()) {
            errors.append("- Participant is required\n");
        }

        if (appointmentDatePicker.getValue() == null) {
            errors.append("- Appointment date is required\n");
        }

        if (appointmentTimeField.getText().trim().isEmpty()) {
            errors.append("- Appointment time is required\n");
        } else if (!appointmentTimeField.getText().matches("\\d{1,2}:\\d{2}")) {
            errors.append("- Appointment time must be in HH:MM format (e.g., 14:30)\n");
        }

        if (statusComboBox.getValue() == null || statusComboBox.getValue().isEmpty()) {
            errors.append("- Status is required\n");
        }

        if (errors.length() > 0) {
            showAlert("Validation Error", "Please correct the following errors:\n\n" + errors.toString());
            return false;
        }

        return true;
    }

    private void populateFormWithAppointment(Appointment appointment) {
        titleField.setText(appointment.getTitle());
        participantField.setText(appointment.getParticipant());
        
        try {
            LocalDate date = LocalDate.parse(appointment.getAppointmentDate(), 
                DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            appointmentDatePicker.setValue(date);
        } catch (Exception e) {
            appointmentDatePicker.setValue(null);
        }
        
        appointmentTimeField.setText(appointment.getAppointmentTime());
        descriptionArea.setText(appointment.getDescription());
        statusComboBox.setValue(appointment.getStatus());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Static method para ma set ang appointment if edited
    public static void setEditingAppointment(Appointment appointment) {
        editingAppointment = appointment;
    }
}