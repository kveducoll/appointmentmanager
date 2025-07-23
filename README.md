## Quick Start

### Prerequisites Check
- [ ] Java JDK 11+ installed
- [ ] Maven 3.6+ installed  
- [ ] JavaFX runtime available

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
- Application window opens with custom dark title bar
- "Appointment Manager (Version 1)" header visible
- Empty table ready for appointments
- Buttons: Add, Edit, Delete, Refresh available

## Usage Guide

### Adding an Appointment
1. Click the **"Add Appointment"** button
2. Fill in the form fields:
   - **Title**: What the appointment is about
   - **Participant**: Person involved in the appointment
   - **Date**: Select appointment date using the date picker
   - **Time**: Enter time in HH:MM format (e.g., 14:30)
   - **Description**: Additional details about the appointment
   - **Status**: Select from dropdown (Scheduled, Confirmed, etc.)
3. Click **"Save Appointment"** to add to the list

### Editing an Appointment
1. Select an appointment from the table
2. Click **"Edit Appointment"** button
3. Modify the fields as needed
4. Click **"Save Appointment"** to update

### Deleting an Appointment
1. Select an appointment from the table
2. Click **"Delete Appointment"** button
3. Confirm deletion in the popup dialog

## Project Structure

```
appointmentmanager/
├── pom.xml                                  # Maven configuration
├── README.md                                # This documentation
└── src/
    └── main/
        ├── java/
        │   └── cpe121/group3/
        │       ├── App.java                 # Main application class
        │       ├── Appointment.java         # Appointment data model
        │       ├── AppointmentManager.java  # Data management singleton
        │       ├── PrimaryController.java   # Main view controller
        │       └── SecondaryController.java # Form view controller
        └── resources/
            └── cpe121/group3/
                ├── primary.fxml             # Main view layout
                └── secondary.fxml           # Form view layout
```

## Architecture

### Model
- **Model**: `Appointment.java` and `AppointmentManager.java`
- **View**: FXML files (`primary.fxml`, `secondary.fxml`)
- **Controller**: `PrimaryController.java` and `SecondaryController.java`

### Key Classes

#### `App.java`
- Main application entry point
- Configures the JavaFX stage with custom title bar
- Handles scene management and FXML loading

#### `Appointment.java`
- Data model representing a single appointment
- Uses JavaFX Properties for data binding
- Fields: title, participant, date, time, description, status

#### `AppointmentManager.java`
- Singleton pattern for managing appointment data
- Provides CRUD operations (Create, Read, Update, Delete)
- Uses ObservableList for real-time UI updates

#### `PrimaryController.java`
- Controls the main application view
- Handles table display and appointment operations
- Implements custom window controls (minimize, maximize, close, drag)

#### `SecondaryController.java`
- Controls the appointment form view
- Handles form validation and data input
- Manages both add and edit modes

## Data Storage

The application uses **in-memory storage** with JavaFX ObservableList:
- No database setup required
- Data persists during application session
- Data is lost when application closes
- SQL Will be added soon

## Troubleshooting

### Common Issues

**Application won't start**
- Ensure Java 11+ is installed
- Verify JavaFX modules are available
- Check Maven is properly configured

**FXML Loading Errors**
- Verify FXML files are in correct resource location
- Check controller class names match FXML fx:controller attributes
- Ensure all @FXML annotations are present

**Window Controls Not Working**
- Verify StageStyle.UNDECORATED is set in App.java
- Check mouse event handlers are properly connected
- Ensure controller methods are public and have @FXML annotation

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

### Code Style
- Follow Java naming conventions
- Add comments for complex logic
- Maintain consistent indentation

## Version History

- **Version 1.0**: Initial release with core appointment management features
- Custom title bar implementation
- Dark theme styling
- CRUD operations for appointments
- Form validation and error handling