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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import java.io.File;
import javafx.application.Platform;
import javafx.print.PrinterJob;
import javafx.print.PageLayout;
import javafx.print.Printer;
import javafx.print.PageOrientation;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Scale;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.collections.transformation.FilteredList;

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
    @FXML private Button printButton;
    @FXML private Button filterButton;
    @FXML private TextField searchField;

    private AppointmentManager appointmentManager;
    private FilteredList<Appointment> filteredAppointments;
    private FilterCriteria currentFilterCriteria; // Store current filter criteria
    private double xOffset = 0;
    private double yOffset = 0;
    private boolean isMaximized = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        appointmentManager = AppointmentManager.getInstance();
        
        // Initialize filter criteria
        currentFilterCriteria = new FilterCriteria();
        
        // Set up table columns
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        participantColumn.setCellValueFactory(new PropertyValueFactory<>("participant"));
        appointmentDateColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        appointmentTimeColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Prepare search bar
        filteredAppointments = new FilteredList<>(appointmentManager.getAppointments(), p -> true);
        appointmentTable.setItems(filteredAppointments);
        setupSearchFunctionality();
        
        updateStatusLabel();
    }

    // Setup search bar 
    private void setupSearchFunctionality() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            applyAllFilters();
        });
    }

    // Proper implementation if filter is active and search bar is used
    private void applyAllFilters() {
        filteredAppointments.setPredicate(appointment -> {
            // First check search filter
            boolean matchesSearch = checkSearchFilter(appointment);
            
            // Then check advanced filters
            boolean matchesAdvancedFilters = checkAdvancedFilters(appointment);
            
            // Both must be true
            return matchesSearch && matchesAdvancedFilters;
        });
        
        // Update status label to show filtered results
        updateStatusLabelWithSearch();
    }

    private boolean checkSearchFilter(Appointment appointment) {
        String searchText = searchField.getText();
        
        // If search field is empty, return true
        if (searchText == null || searchText.trim().isEmpty()) {
            return true;
        }
        
        searchText = searchText.toLowerCase().trim();
        
        // Search in title and participant fields (case-insensitive)
        String title = appointment.getTitle() != null ? appointment.getTitle().toLowerCase() : "";
        String participant = appointment.getParticipant() != null ? appointment.getParticipant().toLowerCase() : "";
        
        boolean matchesTitle = title.contains(searchText);
        boolean matchesParticipant = participant.contains(searchText);
        
        return matchesTitle || matchesParticipant;
    }

    private boolean checkAdvancedFilters(Appointment appointment) {
        // If no advanced filters are active, return true
        if (currentFilterCriteria == null || !currentFilterCriteria.hasActiveFilters()) {
            return true;
        }
        
        // Check each filter condition
        boolean matches = true;
        
        // Title filter
        if (currentFilterCriteria.isTitleFilterEnabled()) {
            String titleFilter = currentFilterCriteria.getTitleFilter();
            if (titleFilter != null && !titleFilter.isEmpty()) {
                String appointmentTitle = appointment.getTitle() != null ? appointment.getTitle() : "";
                matches = matches && matchesText(appointmentTitle, titleFilter, 
                    currentFilterCriteria.isCaseSensitive(), currentFilterCriteria.isExactMatch());
            }
        }
        
        // Participant filter
        if (currentFilterCriteria.isParticipantFilterEnabled()) {
            String participantFilter = currentFilterCriteria.getParticipantFilter();
            if (participantFilter != null && !participantFilter.isEmpty()) {
                String appointmentParticipant = appointment.getParticipant() != null ? appointment.getParticipant() : "";
                matches = matches && matchesText(appointmentParticipant, participantFilter, 
                    currentFilterCriteria.isCaseSensitive(), currentFilterCriteria.isExactMatch());
            }
        }
        
        // Date range filter
        if (currentFilterCriteria.isDateRangeFilterEnabled()) {
            String appointmentDateStr = appointment.getAppointmentDate();
            if (appointmentDateStr != null && !appointmentDateStr.isEmpty()) {
                matches = matches && matchesDateRange(appointmentDateStr, 
                    currentFilterCriteria.getFromDate(), currentFilterCriteria.getToDate());
            }
        }
        
        // Status filter
        if (currentFilterCriteria.isStatusFilterEnabled()) {
            String statusFilter = currentFilterCriteria.getStatusFilter();
            if (statusFilter != null && !statusFilter.isEmpty()) {
                String appointmentStatus = appointment.getStatus() != null ? appointment.getStatus() : "";
                matches = matches && statusFilter.equals(appointmentStatus);
            }
        }
        
        // Description filter
        if (currentFilterCriteria.isDescriptionFilterEnabled()) {
            String descriptionFilter = currentFilterCriteria.getDescriptionFilter();
            if (descriptionFilter != null && !descriptionFilter.isEmpty()) {
                String appointmentDescription = appointment.getDescription() != null ? appointment.getDescription() : "";
                matches = matches && matchesText(appointmentDescription, descriptionFilter, 
                    currentFilterCriteria.isCaseSensitive(), currentFilterCriteria.isExactMatch());
            }
        }
        
        return matches;
    }

    // Apply advanced filters to the table
    public void applyAdvancedFilter(FilterCriteria criteria) {
        // Store the current filter criteria to make it persistent
        this.currentFilterCriteria = criteria;
        
        // Apply all filters (search + advanced)
        applyAllFilters();
    }

    private boolean matchesText(String text, String filter, boolean caseSensitive, boolean exactMatch) {
        if (text == null || filter == null) {
            return false;
        }
        
        String textToCheck = caseSensitive ? text : text.toLowerCase();
        String filterToCheck = caseSensitive ? filter : filter.toLowerCase();
        
        if (exactMatch) {
            return textToCheck.equals(filterToCheck);
        } else {
            return textToCheck.contains(filterToCheck);
        }
    }

    private boolean matchesDateRange(String appointmentDateStr, java.time.LocalDate fromDate, java.time.LocalDate toDate) {
        try {
            // Parse the appointment date string (assuming format: yyyy-MM-dd)
            java.time.LocalDate appointmentDate = java.time.LocalDate.parse(appointmentDateStr);
            
            // Check if within range
            boolean afterFrom = (fromDate == null) || !appointmentDate.isBefore(fromDate);
            boolean beforeTo = (toDate == null) || !appointmentDate.isAfter(toDate);
            
            return afterFrom && beforeTo;
        } catch (Exception e) {
            // If date parsing fails, exclude from results
            return false;
        }
    }

    // Clear filter then show all appointment
    public void clearAllFilters() {
        // Clear the stored filter criteria
        currentFilterCriteria = new FilterCriteria();
        
        // Reapply all filters
        applyAllFilters();
    }

    // Clear search bar function
    @FXML
    private void clearSearch() {
        searchField.clear();
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

    @FXML
    private void updateSelectedAppointmentStatus() throws IOException {
        Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment != null) {
            StatusUpdateController.setSelectedAppointment(selectedAppointment);
            openStatusUpdatePopup();
        } else {
            showAlert("No Selection", "Please select an appointment to update status.");
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

    private void openStatusUpdatePopup() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("statusupdate.fxml"));
        Parent root = loader.load();
        
        Stage popupStage = new Stage();
        popupStage.initStyle(StageStyle.UNDECORATED); // Remove native title bar
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(App.getPrimaryStage());
        
        Scene scene = new Scene(root, 380, 280);
        popupStage.setScene(scene);
        popupStage.setTitle("Update Status");
        popupStage.setResizable(false);
        
        StatusUpdateController controller = loader.getController();
        controller.setPopupStage(popupStage);
        
        popupStage.showAndWait();
        
        refreshTable();
    }

    @FXML
    private void openFilterPopup() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("filter.fxml"));
        Parent root = loader.load();
        
        Stage popupStage = new Stage();
        popupStage.initStyle(StageStyle.UNDECORATED); // Remove native title bar
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(App.getPrimaryStage());
        
        Scene scene = new Scene(root, 490, 450);
        popupStage.setScene(scene);
        popupStage.setTitle("Filter Appointments");
        popupStage.setResizable(false);
        
        FilterController controller = loader.getController();
        controller.setPopupStage(popupStage);
        controller.setTableViewController(this);
        
        popupStage.showAndWait();
        
        // Refresh table after filter is applied
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
                updateStatusLabelWithSearch();
                statusLabel.setText("Appointment deleted successfully.");
            }
        } else {
            showAlert("No Selection", "Please select an appointment to delete.");
        }
    }

    @FXML
    private void refreshTable() {
        appointmentTable.refresh();
        
        // Reapply all filters to maintain persistence
        applyAllFilters();
        
        statusLabel.setText("Table refreshed.");
    }

    public FilterCriteria getCurrentFilterCriteria() {
        return currentFilterCriteria;
    }

    private void updateStatusLabel() {
        int totalAppointments = appointmentManager.getAppointments().size();
        statusLabel.setText("Total appointments: " + totalAppointments);
    }

    private void updateStatusLabelWithSearch() {
        int filteredCount = filteredAppointments.size();
        int totalCount = appointmentManager.getAppointments().size();
        
        String statusText = "";
        boolean hasSearch = searchField.getText() != null && !searchField.getText().trim().isEmpty();
        boolean hasAdvancedFilters = currentFilterCriteria != null && currentFilterCriteria.hasActiveFilters();
        
        if (!hasSearch && !hasAdvancedFilters) {
            statusText = "Total appointments: " + totalCount;
        } else {
            statusText = "Showing " + filteredCount + " of " + totalCount + " appointments";
            
            // Add filter info
            if (hasSearch && hasAdvancedFilters) {
                statusText += " (search + filters active)";
            } else if (hasSearch) {
                statusText += " (search active)";
            } else if (hasAdvancedFilters) {
                statusText += " (filters active)";
            }
        }
        
        statusLabel.setText(statusText);
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

    @FXML
    private void saveAppointments() {
        if (appointmentManager.isFileOpen()) {
            // Save to current file
            if (appointmentManager.save()) {
                statusLabel.setText("Appointments saved successfully.");
            } else {
                statusLabel.setText("Failed to save appointments.");
            }
        } else {
            // No file open, prompt for save as
            saveAppointmentsAs();
        }
    }

    @FXML
    private void saveAppointmentsAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Appointments");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Appointment Files", "*.apf")
        );
        fileChooser.setInitialFileName("appointments.apf");

        File file = fileChooser.showSaveDialog(App.getPrimaryStage());
        if (file != null) {
            String filePath = file.getAbsolutePath();
            if (appointmentManager.saveToFile(filePath)) {
                statusLabel.setText("Appointments saved to: " + file.getName());
            } else {
                showAlert("Save Error", "Failed to save appointments to file.");
            }
        }
    }

    @FXML
    private void loadAppointments() {
        // Check if there are unsaved changes
        if (appointmentManager.getAppointments().size() > 0 && !appointmentManager.isFileOpen()) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Unsaved Changes");
            confirmAlert.setHeaderText("Load New File");
            confirmAlert.setContentText("You have unsaved appointments. Loading a new file will replace them. Continue?");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return;
            }
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Appointments");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Appointment Files", "*.apf")
        );

        File file = fileChooser.showOpenDialog(App.getPrimaryStage());
        if (file != null) {
            String filePath = file.getAbsolutePath();
            if (appointmentManager.loadFromFile(filePath)) {
                updateStatusLabelWithSearch();
                statusLabel.setText("Appointments loaded from: " + file.getName());
            } else {
                showAlert("Load Error", "Failed to load appointments from file.");
            }
        }
    }

    @FXML
    private void newAppointmentFile() {
        // Check if there are unsaved changes
        if (appointmentManager.getAppointments().size() > 0 && !appointmentManager.isFileOpen()) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Unsaved Changes");
            confirmAlert.setHeaderText("Create New File");
            confirmAlert.setContentText("You have unsaved appointments. Creating a new file will clear them. Continue?");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return;
            }
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Create New Appointment File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Appointment Files", "*.apf")
        );
        fileChooser.setInitialFileName("new_appointments.apf");

        File file = fileChooser.showSaveDialog(App.getPrimaryStage());
        if (file != null) {
            String filePath = file.getAbsolutePath();
            if (appointmentManager.createNewFile(filePath)) {
                updateStatusLabelWithSearch();
                statusLabel.setText("New appointment file created: " + file.getName());
            } else {
                showAlert("Creation Error", "Failed to create new appointment file.");
            }
        }
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

    @FXML
    private void goToMainMenu() {
        try {
            App.setRoot("mainmenu");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load mainmenu.fxml");
        }
    }

    @FXML
    private void printAppointments() {
        PrinterJob printerJob = PrinterJob.createPrinterJob();
        if (printerJob != null && printerJob.showPrintDialog(App.getPrimaryStage())) {
            
            // Set margins
            double leftMargin = 50;  
            double topMargin = 50;  
            double rightMargin = 50;  
            double bottomMargin = 50;  
            
            // Configure page layout
            Printer printer = printerJob.getPrinter();
            PageLayout pageLayout = printer.createPageLayout(
                printer.getDefaultPageLayout().getPaper(),
                PageOrientation.PORTRAIT,
                leftMargin, rightMargin, topMargin, bottomMargin
            );
            printerJob.getJobSettings().setPageLayout(pageLayout);
            
            // Print appointments with pagination
            boolean success = printAppointmentsWithPagination(printerJob, pageLayout);
            
            if (success) {
                printerJob.endJob();
                statusLabel.setText("All " + appointmentManager.getAppointments().size() + " appointments printed successfully across multiple pages.");
            } else {
                statusLabel.setText("Printing failed.");
            }
        } else {
            statusLabel.setText("Printing cancelled or no printer available.");
        }
    }
    
    private boolean printAppointmentsWithPagination(PrinterJob printerJob, PageLayout pageLayout) {
        int appointmentsPerPage = 10;
        int totalAppointments = appointmentManager.getAppointments().size();
        int totalPages = (int) Math.ceil((double) totalAppointments / appointmentsPerPage);
        
        for (int page = 0; page < totalPages; page++) {
            int startIndex = page * appointmentsPerPage;
            int endIndex = Math.min(startIndex + appointmentsPerPage, totalAppointments);
            
            VBox pageContent = createPageContent(startIndex, endIndex, page + 1, totalPages);
            
            // Content Scaling
            Scale scale = new Scale(0.8, 0.8);
            pageContent.getTransforms().add(scale);
            
            boolean pageSuccess = printerJob.printPage(pageContent);
            
            // Clean up
            pageContent.getTransforms().remove(scale);
            
            if (!pageSuccess) {
                return false;
            }
        }
        
        return true;
    }
    
    private VBox createPageContent(int startIndex, int endIndex, int currentPage, int totalPages) {
        VBox content = new VBox(8);
        
        // Page header
        Label header = new Label("APPOINTMENT MANAGER REPORT");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        content.getChildren().add(header);
        
        Label groupInfo = new Label("GROUP 3 (CPE 121 FINAL PROJECT)");
        groupInfo.setFont(Font.font("Arial", 10));
        content.getChildren().add(groupInfo);

        Label dateTime = new Label("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        dateTime.setFont(Font.font("Arial", 10));
        content.getChildren().add(dateTime);
        
        Label pageInfo = new Label("Page " + currentPage + " of " + totalPages);
        pageInfo.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        content.getChildren().add(pageInfo);
        
        // Separator
        Label separator = new Label("=".repeat(250));
        separator.setFont(Font.font("Arial", 10));
        content.getChildren().add(separator);
        
        // Column headers
        Label columnHeaders = new Label(String.format("%-4s %-25s %-20s %-12s %-10s %-15s", 
            "#", "Title", "Participant", "Date", "Time", "Status"));
        columnHeaders.setFont(Font.font("Courier New", FontWeight.BOLD, 12));
        content.getChildren().add(columnHeaders);
        
        Label headerSeparator = new Label("-".repeat(250));
        headerSeparator.setFont(Font.font("Arial", 10));
        content.getChildren().add(headerSeparator);
        
        // Add appointments for this page
        for (int i = startIndex; i < endIndex; i++) {
            Appointment appointment = appointmentManager.getAppointments().get(i);
            
            String appointmentLine = String.format("%-4d %-25s %-20s %-12s %-10s %-15s", 
                i + 1,
                truncateString(appointment.getTitle(), 24),
                truncateString(appointment.getParticipant(), 19),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                appointment.getStatus()
            );
            
            // Appointment Content Handler (Per table row ni)
            Label appointmentLabel = new Label(appointmentLine);
            appointmentLabel.setFont(Font.font("Courier New", 12)); // 12 ang default
            content.getChildren().add(appointmentLabel);
            
            // Description handler
            if (appointment.getDescription() != null && !appointment.getDescription().trim().isEmpty()) {
                String description = "     Description: " + truncateString(appointment.getDescription(), 70);
                Label descLabel = new Label(description);
                descLabel.setFont(Font.font("Arial", 10));
                content.getChildren().add(descLabel);
            }
            
           // Spacer
            Label spacer = new Label("");
            spacer.setFont(Font.font("Arial", 4));
            content.getChildren().add(spacer);
        }
        
        // Footer
        Label footer = new Label("Showing appointments " + (startIndex + 1) + " to " + endIndex + " of " + appointmentManager.getAppointments().size());
        footer.setFont(Font.font("Arial", 9));
        content.getChildren().add(footer);
        
        return content;
    }
    
    private String truncateString(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }
}
