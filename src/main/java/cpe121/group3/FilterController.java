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
    private TableViewController tableViewController;
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

    public void setTableViewController(TableViewController tableViewController) {
        this.tableViewController = tableViewController;
        
        // Populate UI with current filter values
        if (tableViewController != null) {
            populateCurrentFilters();
        }
    }

    //Populate UI withcurrent filter values
    private void populateCurrentFilters() {
        FilterCriteria currentCriteria = tableViewController.getCurrentFilterCriteria();
        if (currentCriteria == null) {
            return;
        }
        
        // Title filter
        titleFilterEnabled.setSelected(currentCriteria.isTitleFilterEnabled());
        if (currentCriteria.getTitleFilter() != null) {
            titleFilterField.setText(currentCriteria.getTitleFilter());
        }
        
        // Participant filter
        participantFilterEnabled.setSelected(currentCriteria.isParticipantFilterEnabled());
        if (currentCriteria.getParticipantFilter() != null) {
            participantFilterField.setText(currentCriteria.getParticipantFilter());
        }
        
        // Date range filter
        dateRangeFilterEnabled.setSelected(currentCriteria.isDateRangeFilterEnabled());
        fromDatePicker.setValue(currentCriteria.getFromDate());
        toDatePicker.setValue(currentCriteria.getToDate());
        
        // Status filter
        statusFilterEnabled.setSelected(currentCriteria.isStatusFilterEnabled());
        if (currentCriteria.getStatusFilter() != null) {
            statusFilterComboBox.setValue(currentCriteria.getStatusFilter());
        }
        
        // Description filter
        descriptionFilterEnabled.setSelected(currentCriteria.isDescriptionFilterEnabled());
        if (currentCriteria.getDescriptionFilter() != null) {
            descriptionFilterField.setText(currentCriteria.getDescriptionFilter());
        }
        
        // Options
        caseSensitiveCheckBox.setSelected(currentCriteria.isCaseSensitive());
        exactMatchCheckBox.setSelected(currentCriteria.isExactMatch());
    }

    @FXML
    private void applyFilter() {
        if (tableViewController == null) {
            System.err.println("TableViewController reference not set");
            closeFilter();
            return;
        }

        // Create filter criteria from UI inputs
        FilterCriteria criteria = createFilterCriteria();
        
        // Apply filter to table view
        tableViewController.applyAdvancedFilter(criteria);
        
        closeFilter();
    }

    
    private FilterCriteria createFilterCriteria() {
        FilterCriteria criteria = new FilterCriteria();
        
        // Title filter
        criteria.setTitleFilterEnabled(titleFilterEnabled.isSelected());
        if (criteria.isTitleFilterEnabled() && titleFilterField.getText() != null) {
            criteria.setTitleFilter(titleFilterField.getText().trim());
        }
        
        // Participant filter
        criteria.setParticipantFilterEnabled(participantFilterEnabled.isSelected());
        if (criteria.isParticipantFilterEnabled() && participantFilterField.getText() != null) {
            criteria.setParticipantFilter(participantFilterField.getText().trim());
        }
        
        // Date range filter
        criteria.setDateRangeFilterEnabled(dateRangeFilterEnabled.isSelected());
        if (criteria.isDateRangeFilterEnabled()) {
            criteria.setFromDate(fromDatePicker.getValue());
            criteria.setToDate(toDatePicker.getValue());
        }
        
        // Status filter
        criteria.setStatusFilterEnabled(statusFilterEnabled.isSelected());
        if (criteria.isStatusFilterEnabled() && statusFilterComboBox.getValue() != null) {
            criteria.setStatusFilter(statusFilterComboBox.getValue());
        }
        
        // Description filter
        criteria.setDescriptionFilterEnabled(descriptionFilterEnabled.isSelected());
        if (criteria.isDescriptionFilterEnabled() && descriptionFilterField.getText() != null) {
            criteria.setDescriptionFilter(descriptionFilterField.getText().trim());
        }
        
        // Options
        criteria.setCaseSensitive(caseSensitiveCheckBox.isSelected());
        criteria.setExactMatch(exactMatchCheckBox.isSelected());
        
        return criteria;
    }

    @FXML
    private void clearAllFilters() {
        // Clear UI fields
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
        
        // Clear filters in table view
        if (tableViewController != null) {
            tableViewController.clearAllFilters();
        }
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
