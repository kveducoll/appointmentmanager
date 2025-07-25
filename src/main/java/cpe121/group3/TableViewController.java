package cpe121.group3;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Modality;
import javafx.application.Platform;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Optional;

public class TableViewController implements Initializable {

    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, String> titleColumn;
    @FXML private TableColumn<Appointment, String> participantColumn;
    @FXML private TableColumn<Appointment, String> appointmentDateColumn;
    @FXML private TableColumn<Appointment, String> appointmentTimeColumn;
    @FXML private TableColumn<Appointment, String> descriptionColumn;
    @FXML private TableColumn<Appointment, String> statusColumn;
    @FXML private Label statusLabel;
    @FXML private HBox titleBar;

    private AppointmentManager appointmentManager;
    private double xOffset = 0;
    private double yOffset = 0;
    private boolean isMaximized = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        appointmentManager = AppointmentManager.getInstance();
        
        // Set up table columns
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        participantColumn.setCellValueFactory(new PropertyValueFactory<>("participant"));
        appointmentDateColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        appointmentTimeColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Load data if existing to the table (Pre DB implementation)
        appointmentTable.setItems(appointmentManager.getAppointments());
        updateStatusLabel();
    }

    @FXML
    private void showAddAppointment() throws IOException {
        // Clean up appointment data from popup form
        PopupFormController.setEditingAppointment(null);
        openAppointmentPopup();
    }

    // Handle the editing for appointment
    @FXML
    private void editSelectedAppointment() throws IOException {
        Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment != null) {
            PopupFormController.setEditingAppointment(selectedAppointment);
            openAppointmentPopup();
        } else {
            showAlert("No Selection", "Please select an appointment to edit.");
        }
    }

    private void openAppointmentPopup() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("popupform.fxml"));
        Parent root = loader.load();
        
        Stage popupStage = new Stage();
        popupStage.initStyle(StageStyle.UNDECORATED); // Remove native title bar
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(App.getPrimaryStage());
        
        Scene scene = new Scene(root, 450, 400);
        popupStage.setScene(scene);
        popupStage.setTitle("Appointment Form");
        popupStage.setResizable(false);
        
        PopupFormController controller = loader.getController();
        controller.setPopupStage(popupStage);
        
        popupStage.showAndWait();
        
        refreshTable();
    }

    @FXML
    private void deleteSelectedAppointment() {
        Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Deletion");
            confirmAlert.setHeaderText("Delete Appointment");
            confirmAlert.setContentText("Are you sure you want to delete the appointment '" + 
                                      selectedAppointment.getTitle() + "'?");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                appointmentManager.deleteAppointment(selectedAppointment);
                updateStatusLabel();
                statusLabel.setText("Appointment deleted successfully.");
            }
        } else {
            showAlert("No Selection", "Please select an appointment to delete.");
        }
    }

    @FXML
    private void refreshTable() {
        appointmentTable.refresh();
        updateStatusLabel();
        statusLabel.setText("Table refreshed.");
    }

    private void updateStatusLabel() {
        int totalAppointments = appointmentManager.getAppointments().size();
        statusLabel.setText("Total appointments: " + totalAppointments);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void onReturnFromPopupForm() {
        refreshTable();
    }

    // Custom title bar window controls
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
        if (isMaximized) {
            stage.setMaximized(false);
            isMaximized = false;
        } else {
            stage.setMaximized(true);
            isMaximized = true;
        }
    }

    @FXML
    private void closeWindow() {
        Platform.exit();
    }
}
