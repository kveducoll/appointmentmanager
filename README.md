## Quick Start

### Prerequisites
- Java JDK 11+ installed
- Maven 3.6+ installed  
- JavaFX runtime available

### Build and Run (5 minutes)
```bash
# Verify Java
java -version  # Should show version 11+

# Verify Maven
mvn -version   # Should show Maven 3.6+

# Navigate to project
cd appointmentmanager

# Build and run
mvn clean compile javafx:run
```

### Expected Result
- Application window opens with a welcome screen.
- Options to create a new appointment file or load an existing one.
- Drag-and-drop support for `.apf` (Appointment File) files.

## Usage Guide

### Creating or Loading an Appointment File
1.  Launch the application.
2.  On the main menu, click **"New"** to create a new `.apf` file or **"Load"** to open an existing one.
3.  You can also drag and drop an `.apf` file onto the application window.

### Adding an Appointment
1. Once an appointment file is loaded, the table view will be displayed.
2. Click the **"Add"** button.
3. Fill in the form fields:
   - **Title**: What the appointment is about
   - **Participant**: Person involved in the appointment
   - **Date**: Select appointment date using the date picker
   - **Time**: Enter time in HH:MM format (e.g., 14:30)
   - **Description**: Additional details about the appointment
   - **Status**: Select from dropdown (Scheduled, Confirmed, etc.)
4. Click **"Save"** to add to the list.

### Editing an Appointment
1. Select an appointment from the table.
2. Click the **"Edit"** button.
3. Modify the fields as needed.
4. Click **"Save"** to update.

### Deleting an Appointment
1. Select an appointment from the table.
2. Click the **"Delete"** button.
3. Confirm deletion in the popup dialog.

### Filtering Appointments
1. Click the **"Filter"** button to open the filter options.
2. Enable and specify criteria for title, participant, date range, status, or description.
3. Click **"Apply Filter"** to see the results.

### Updating Status
1. Right-click an appointment and select **"Update Status"**.
2. Choose a new status from the dropdown.
3. Click **"Update"** to save the change.

## Project Structure

```
appointmentmanager/
├── pom.xml                                             # Maven configuration
├── README.md                                           # This documentation
└── src/
    └── main/
        ├── java/
        │   └── cpe121/group3/
        │       ├── App.java                            # Main application class
        │       ├── Appointment.java                    # Appointment data model
        │       ├── AppointmentManager.java             # Data management singleton
        │       ├── DatabaseManager.java                # SQLite database handler
        │       ├── MainMenuController.java             # Main menu controller
        │       ├── TableViewController.java            # Table view controller
        │       ├── PopupFormController.java            # Add/Edit form controller
        │       ├── FilterController.java               # Filter form controller
        │       ├── StatusUpdateController.java         # Status update controller
        │       └── AboutController.java                # About page controller
        └── resources/
            └── cpe121/group3/
                ├── mainmenu.fxml                       # Main menu layout
                ├── tableview.fxml                      # Table view layout
                ├── popupform.fxml                      # Add/Edit form layout
                ├── filter.fxml                         # Filter form layout
                ├── statusupdate.fxml                   # Status update layout
                ├── about.fxml                          # About page layout
                ├── assets/
                │   └── Appointment-Manager-Logo.png    # Application logo
                ├── iconpack/                           # UI icons for buttons and interface
                └── style/
                    └── *.css                           # CSS stylesheets
```

## Architecture

### Model-View-Controller (MVC)
- **Model**: `Appointment.java`, `AppointmentManager.java`, `DatabaseManager.java`
- **View**: FXML files (`mainmenu.fxml`, `tableview.fxml`, `popupform.fxml`, etc.)
- **Controller**: `MainMenuController.java`, `TableViewController.java`, `PopupFormController.java`, `FilterController.java`, `StatusUpdateController.java`, `AboutController.java`

### Key Classes

#### `App.java`
- Main application entry point.
- Configures the JavaFX stage and loads the initial `mainmenu.fxml` scene.

#### `Appointment.java`
- Data model for a single appointment, using JavaFX Properties for data binding.

#### `AppointmentManager.java`
- Singleton for managing the `ObservableList` of appointments in memory.

#### `DatabaseManager.java`
- Handles all SQLite database operations (CRUD) for storing and retrieving appointments.

#### `MainMenuController.java`
- Controls the main welcome screen.
- Handles creating new, loading, and drag-and-dropping `.apf` database files.

#### `TableViewController.java`
- Manages the main table view displaying appointments.
- Integrates with controllers for adding, editing, deleting, filtering, and updating status.

#### `PopupFormController.java`
- Controls the popup form for adding and editing appointments.

#### `FilterController.java`
- Manages the filtering UI and applies `FilterCriteria` to the appointment list.

#### `StatusUpdateController.java`
- Controls the UI for quick status updates on an appointment.

## Data Storage

The application uses **SQLite** for data persistence.
- Appointments are stored in a local file with an `.apf` extension.
- The `DatabaseManager` class handles all interactions with the SQLite database.
- Data is saved automatically upon modification.

## Troubleshooting

### Common Issues

**Application won't start**
- Ensure Java 11+ and Maven are installed and configured correctly.
- Verify JavaFX modules are available.

**FXML Loading Errors**
- Check that FXML files are in the correct resource location.
- Ensure `fx:controller` attributes in FXML match the controller class paths.

**Database Connection Errors**
- Ensure you have permissions to write files in the chosen directory.
- Verify the `.apf` file is not corrupted.

## Development

### Building for Development
```bash
# Compile only
mvn compile

# Clean and compile
mvn clean compile

# Run with Maven
mvn javafx:run
```