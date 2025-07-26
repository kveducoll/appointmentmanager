package cpe121.group3;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;

/**
 * Singleton class that manages appointment operations and database persistence.
 * Provides API for CRUD operations on appointments with automatic database synchronization.
 * Uses .apf (Appointment File) format for data storage via SQLite.
 */
public class AppointmentManager {
    private static AppointmentManager instance;
    private ObservableList<Appointment> appointments;
    private DatabaseManager databaseManager;
    private String currentFilePath;

    private AppointmentManager() {
        appointments = FXCollections.observableArrayList();
        databaseManager = new DatabaseManager();
    }

    public static AppointmentManager getInstance() {
        if (instance == null) {
            instance = new AppointmentManager();
        }
        return instance;
    }

    public ObservableList<Appointment> getAppointments() {
        return appointments;
    }

    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
        
        // Save to database if connected
        if (databaseManager.isConnected()) {
            if (!databaseManager.saveAppointment(appointment)) {
                System.err.println("Failed to save appointment to database");
            }
        }
    }

    public void updateAppointment(int index, Appointment appointment) {
        if (index >= 0 && index < appointments.size()) {
            Appointment oldAppointment = appointments.get(index);
            appointments.set(index, appointment);
            
            // Update in database if connected
            if (databaseManager.isConnected()) {
                if (!databaseManager.updateAppointment(oldAppointment, appointment)) {
                    System.err.println("Failed to update appointment in database");
                }
            }
        }
    }

    public void deleteAppointment(Appointment appointment) {
        appointments.remove(appointment);
        
        // Delete from database if connected
        if (databaseManager.isConnected()) {
            if (!databaseManager.deleteAppointment(appointment)) {
                System.err.println("Failed to delete appointment from database");
            }
        }
    }

    public void deleteAppointment(int index) {
        if (index >= 0 && index < appointments.size()) {
            Appointment appointment = appointments.get(index);
            deleteAppointment(appointment);
        }
    }

    public int getAppointmentIndex(Appointment appointment) {
        return appointments.indexOf(appointment);
    }

    public void clearAllAppointments() {
        appointments.clear();
        
        // Clear database if connected
        if (databaseManager.isConnected()) {
            if (!databaseManager.clearAllAppointments()) {
                System.err.println("Failed to clear appointments from database");
            }
        }
    }

    // Save appointments to a new .apf file
    public boolean saveToFile(String filePath) {
        try {
            // Ensure .apf extension
            if (!filePath.toLowerCase().endsWith(".apf")) {
                filePath += ".apf";
            }
            
            // Connect to new database file
            if (databaseManager.connectToDatabase(filePath)) {
                currentFilePath = filePath;
                
                // Save all current appointments to the new database
                for (Appointment appointment : appointments) {
                    if (!databaseManager.saveAppointment(appointment)) {
                        System.err.println("Failed to save appointment: " + appointment.getTitle());
                        return false;
                    }
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error saving to file: " + e.getMessage());
            return false;
        }
    }

    //Load appointments from an existing .apf file
    public boolean loadFromFile(String filePath) {
        try {
            // Ensure .apf extension
            if (!filePath.toLowerCase().endsWith(".apf")) {
                filePath += ".apf";
            }
            
            // Connect to database file
            if (databaseManager.connectToDatabase(filePath)) {
                currentFilePath = filePath;
                
                // Load appointments from database
                List<Appointment> loadedAppointments = databaseManager.loadAppointments();
                
                // Clear current appointments and add loaded ones
                appointments.clear();
                appointments.addAll(loadedAppointments);
                
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error loading from file: " + e.getMessage());
            return false;
        }
    }

    //Save current appointments to the currently open file 
    public boolean save() {
        if (currentFilePath != null && databaseManager.isConnected()) {
            // Current implementation already saves automatically when adding/updating/deleting
            // This method can be used for explicit saves if needed
            return true;
        }
        return false;
    }

    // Create new appointment file
    public boolean createNewFile(String filePath) {
        try {
            // Clear current appointments
            appointments.clear();
            
            // Connect to new database file (this will create it)
            if (databaseManager.connectToDatabase(filePath)) {
                currentFilePath = filePath;
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error creating new file: " + e.getMessage());
            return false;
        }
    }

    // Get file path
    public String getCurrentFilePath() {
        return currentFilePath;
    }

    // Check if file is open
    public boolean isFileOpen() {
        return currentFilePath != null && databaseManager.isConnected();
    }

    // Close current file
    public void closeFile() {
        databaseManager.closeConnection();
        currentFilePath = null;
        appointments.clear();
    }
}
