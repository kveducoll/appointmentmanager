package cpe121.group3;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class FilterController implements Initializable {

    @FXML private VBox titleBar;
    @FXML private Label formTitle;
    
    @FXML private CheckBox titleFilterEnabled;
    @FXML private TextField titleFilterField;
    @FXML private CheckBox participantFilterEnabled;
    @FXML private TextField participantFilterField;
    @FXML private CheckBox dateRangeFilterEnabled;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private CheckBox statusFilterEnabled;
    @FXML private ComboBox<String> statusFilterComboBox;
    @FXML private CheckBox descriptionFilterEnabled;
    @FXML private TextField descriptionFilterField;
    @FXML private CheckBox caseSensitiveCheckBox;
    @FXML private CheckBox exactMatchCheckBox;
    
    // Buttons
    @FXML private Button applyFilterButton;
    @FXML private Button clearAllButton;
    @FXML private Button cancelButton;
    
    private Stage popupStage;
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize status combo box with appointment statuses
        statusFilterComboBox.getItems().addAll("Scheduled", "Completed", "Cancelled", "Pending");
        
        titleFilterField.setDisable(true);
        participantFilterField.setDisable(true);
        fromDatePicker.setDisable(true);
        toDatePicker.setDisable(true);
        statusFilterComboBox.setDisable(true);
        descriptionFilterField.setDisable(true);
        
        titleFilterEnabled.selectedProperty().addListener((obs, oldVal, newVal) -> 
            titleFilterField.setDisable(!newVal));
        participantFilterEnabled.selectedProperty().addListener((obs, oldVal, newVal) -> 
            participantFilterField.setDisable(!newVal));
        dateRangeFilterEnabled.selectedProperty().addListener((obs, oldVal, newVal) -> {
            fromDatePicker.setDisable(!newVal);
            toDatePicker.setDisable(!newVal);
        });
        statusFilterEnabled.selectedProperty().addListener((obs, oldVal, newVal) -> 
            statusFilterComboBox.setDisable(!newVal));
        descriptionFilterEnabled.selectedProperty().addListener((obs, oldVal, newVal) -> 
            descriptionFilterField.setDisable(!newVal));
    }

    public void setPopupStage(Stage popupStage) {
        this.popupStage = popupStage;
    }

    @FXML
    private void applyFilter() {
        // TODO: Implement filter logic
        System.out.println("Apply filter clicked - functionality to be implemented");
        closeFilter();
    }

    @FXML
    private void clearAllFilters() {
        titleFilterEnabled.setSelected(false);
        titleFilterField.clear();
        participantFilterEnabled.setSelected(false);
        participantFilterField.clear();
        dateRangeFilterEnabled.setSelected(false);
        fromDatePicker.setValue(null);
        toDatePicker.setValue(null);
        statusFilterEnabled.setSelected(false);
        statusFilterComboBox.setValue(null);
        descriptionFilterEnabled.setSelected(false);
        descriptionFilterField.clear();
        caseSensitiveCheckBox.setSelected(false);
        exactMatchCheckBox.setSelected(false);
    }

    @FXML
    private void closeFilter() {
        if (popupStage != null) {
            popupStage.close();
        }
    }

    // Title bar drag functionality
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
}
