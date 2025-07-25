package cpe121.group3;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SecondaryController implements Initializable {

    @FXML private VBox titleBar;
    @FXML private Label formTitle;
    @FXML private TextField titleField;
    @FXML private TextField participantField;
    @FXML private DatePicker appointmentDatePicker;
    @FXML private ComboBox<String> hoursComboBox;
    @FXML private ComboBox<String> minutesComboBox;
    @FXML private ComboBox<String> ampmComboBox;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<String> statusComboBox;

    private AppointmentManager appointmentManager;
    private static Appointment editingAppointment = null;
    private boolean isEditMode = false;
    private Stage popupStage;
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        appointmentManager = AppointmentManager.getInstance();
        
        // Initialize Time
        for (int i = 1; i <= 12; i++) {
            hoursComboBox.getItems().add(String.format("%02d", i));
        }
        for (int i = 0; i <= 59; i++) {
            minutesComboBox.getItems().add(String.format("%02d", i));
        }
        ampmComboBox.getItems().addAll("AM", "PM");
        
        // Initiaize Status
        statusComboBox.getItems().addAll(
            "Scheduled", "Confirmed", "In Progress", "Completed", "Cancelled", "No Show"
        );
        statusComboBox.setValue("Scheduled");

        // Check if Existing Appointment
        if (editingAppointment != null) {
            isEditMode = true;
            formTitle.setText("Update Appointment");
            populateFormWithAppointment(editingAppointment);
        } else {
            isEditMode = false;
            formTitle.setText("Appointment Form");
            clearForm();
        }
    }

    @FXML
    private void saveAppointment() throws IOException {
        if (validateForm()) {
            String title = titleField.getText().trim();
            String participant = participantField.getText().trim();
            String appointmentDate = appointmentDatePicker.getValue() != null ? 
                appointmentDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";
            String appointmentTime = getFormattedTime();
            String description = descriptionArea.getText().trim();
            String status = statusComboBox.getValue();

            if (isEditMode && editingAppointment != null) {
                // Update appointment
                editingAppointment.setTitle(title);
                editingAppointment.setParticipant(participant);
                editingAppointment.setAppointmentDate(appointmentDate);
                editingAppointment.setAppointmentTime(appointmentTime);
                editingAppointment.setDescription(description);
                editingAppointment.setStatus(status);
                
                showAlert("Success", "Appointment updated successfully!");
            } else {
                // Create appointment
                Appointment newAppointment = new Appointment(
                    title, participant, appointmentDate, appointmentTime, description, status
                );
                appointmentManager.addAppointment(newAppointment);
                showAlert("Success", "Appointment added successfully!");
            }

            // Clear editing and close popup
            editingAppointment = null;
            if (popupStage != null) {
                popupStage.close();
            }
        }
    }

    @FXML
    private void clearForm() {
        titleField.clear();
        participantField.clear();
        appointmentDatePicker.setValue(null);
        hoursComboBox.setValue(null);
        minutesComboBox.setValue(null);
        ampmComboBox.setValue(null);
        descriptionArea.clear();
        statusComboBox.setValue("Scheduled");
    }

    private String getFormattedTime() {
        String hour = hoursComboBox.getValue();
        String minute = minutesComboBox.getValue();
        String ampm = ampmComboBox.getValue();
        
        if (hour != null && minute != null && ampm != null) {
            return hour + ":" + minute + " " + ampm;
        }
        return "";
    }

    @FXML
    private void switchToPrimary() throws IOException {
        editingAppointment = null;
        if (popupStage != null) {
            popupStage.close();
        }
    }

    public void setPopupStage(Stage popupStage) {
        this.popupStage = popupStage;
    }

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

        if (hoursComboBox.getValue() == null || minutesComboBox.getValue() == null || ampmComboBox.getValue() == null) {
            errors.append("- Complete appointment time is required (Hour, Minute, AM/PM)\n");
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
        
        parseAndSetTime(appointment.getAppointmentTime());
        descriptionArea.setText(appointment.getDescription());
        statusComboBox.setValue(appointment.getStatus());
    }

    // Time parser (Error Prone Zone)
    private void parseAndSetTime(String timeString) {
        if (timeString != null && !timeString.trim().isEmpty()) {
            try {
                if (timeString.contains("AM") || timeString.contains("PM")) {
                    // 12-hour format: "02:30 PM"
                    String[] parts = timeString.split(" ");
                    String[] timeParts = parts[0].split(":");
                    String hour = timeParts[0];
                    String minute = timeParts[1];
                    String ampm = parts[1];
                    
                    hoursComboBox.setValue(hour);
                    minutesComboBox.setValue(minute);
                    ampmComboBox.setValue(ampm);
                } else {
                    // 24-hour format: "14:30" - convert to 12-hour
                    String[] timeParts = timeString.split(":");
                    int hour24 = Integer.parseInt(timeParts[0]);
                    String minute = timeParts[1];
                    
                    String ampm = hour24 >= 12 ? "PM" : "AM";
                    int hour12 = hour24 > 12 ? hour24 - 12 : (hour24 == 0 ? 12 : hour24);
                    
                    hoursComboBox.setValue(String.format("%02d", hour12));
                    minutesComboBox.setValue(minute);
                    ampmComboBox.setValue(ampm);
                }
            } catch (Exception e) {
                // Clear if fail
                hoursComboBox.setValue(null);
                minutesComboBox.setValue(null);
                ampmComboBox.setValue(null);
            }
        }
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