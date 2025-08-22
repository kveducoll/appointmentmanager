package cpe121.group3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// Database manager API and use SQLite as .apf (Appointment File)
public class DatabaseManager {
    private static final String DATABASE_URL_PREFIX = "jdbc:sqlite:";
    private Connection connection;
    private String currentDatabasePath;

    public DatabaseManager() {
    }

    // Connect to or create a new SQLite database with .apf extension
    public boolean connectToDatabase(String filePath) {
        try {
            if (!filePath.toLowerCase().endsWith(".apf")) {
                filePath += ".apf";
            }
            
            this.currentDatabasePath = filePath;
            String url = DATABASE_URL_PREFIX + filePath;

            closeConnection();
            
            connection = DriverManager.getConnection(url);
            createAppointmentsTable();
            
            return true;
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            return false;
        }
    }

    private void createAppointmentsTable() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS appointments (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "title TEXT NOT NULL, " +
            "participant TEXT NOT NULL, " +
            "appointment_date TEXT NOT NULL, " +
            "appointment_time TEXT NOT NULL, " +
            "description TEXT, " +
            "status TEXT NOT NULL DEFAULT 'Scheduled', " +
            "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ")";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    // Save implementation
    public boolean saveAppointment(Appointment appointment) {
        if (connection == null) {
            System.err.println("No database connection available");
            return false;
        }

        String insertSQL = "INSERT INTO appointments (title, participant, appointment_date, appointment_time, description, status) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, appointment.getTitle());
            pstmt.setString(2, appointment.getParticipant());
            pstmt.setString(3, appointment.getAppointmentDate());
            pstmt.setString(4, appointment.getAppointmentTime());
            pstmt.setString(5, appointment.getDescription());
            pstmt.setString(6, appointment.getStatus());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saving appointment: " + e.getMessage());
            return false;
        }
    }

    // Load implementation
    public List<Appointment> loadAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        
        if (connection == null) {
            System.err.println("No database connection available");
            return appointments;
        }

        String selectSQL = "SELECT title, participant, appointment_date, appointment_time, description, status " +
            "FROM appointments " +
            "ORDER BY appointment_date, appointment_time";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {
            
            while (rs.next()) {
                Appointment appointment = new Appointment(
                    rs.getString("title"),
                    rs.getString("participant"),
                    rs.getString("appointment_date"),
                    rs.getString("appointment_time"),
                    rs.getString("description"),
                    rs.getString("status")
                );
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            System.err.println("Error loading appointments: " + e.getMessage());
        }

        return appointments;
    }

   // Update implementation
    public boolean updateAppointment(Appointment oldAppointment, Appointment newAppointment) {
        if (connection == null) {
            System.err.println("No database connection available");
            return false;
        }

        String updateSQL = "UPDATE appointments " +
            "SET title = ?, participant = ?, appointment_date = ?, appointment_time = ?, " +
            "description = ?, status = ?, updated_at = CURRENT_TIMESTAMP " +
            "WHERE title = ? AND participant = ? AND appointment_date = ? AND appointment_time = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
            // New values
            pstmt.setString(1, newAppointment.getTitle());
            pstmt.setString(2, newAppointment.getParticipant());
            pstmt.setString(3, newAppointment.getAppointmentDate());
            pstmt.setString(4, newAppointment.getAppointmentTime());
            pstmt.setString(5, newAppointment.getDescription());
            pstmt.setString(6, newAppointment.getStatus());
            
            // old values
            pstmt.setString(7, oldAppointment.getTitle());
            pstmt.setString(8, oldAppointment.getParticipant());
            pstmt.setString(9, oldAppointment.getAppointmentDate());
            pstmt.setString(10, oldAppointment.getAppointmentTime());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating appointment: " + e.getMessage());
            return false;
        }
    }

    // delete implementation
    public boolean deleteAppointment(Appointment appointment) {
        if (connection == null) {
            System.err.println("No database connection available");
            return false;
        }

        String deleteSQL = "DELETE FROM appointments " +
            "WHERE title = ? AND participant = ? AND appointment_date = ? AND appointment_time = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
            pstmt.setString(1, appointment.getTitle());
            pstmt.setString(2, appointment.getParticipant());
            pstmt.setString(3, appointment.getAppointmentDate());
            pstmt.setString(4, appointment.getAppointmentTime());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting appointment: " + e.getMessage());
            return false;
        }
    }

    // Clear appointments from database
    public boolean clearAllAppointments() {
        if (connection == null) {
            System.err.println("No database connection available");
            return false;
        }

        String deleteSQL = "DELETE FROM appointments";

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(deleteSQL);
            return true;
        } catch (SQLException e) {
            System.err.println("Error clearing appointments: " + e.getMessage());
            return false;
        }
    }

    // Bulk save appointments with transaction for better performance and consistency
    public boolean saveAllAppointments(List<Appointment> appointments) {
        if (connection == null) {
            System.err.println("No database connection available");
            return false;
        }

        String insertSQL = "INSERT INTO appointments (title, participant, appointment_date, appointment_time, description, status) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        try {
            // Start transaction
            connection.setAutoCommit(false);
            
            try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
                for (Appointment appointment : appointments) {
                    pstmt.setString(1, appointment.getTitle());
                    pstmt.setString(2, appointment.getParticipant());
                    pstmt.setString(3, appointment.getAppointmentDate());
                    pstmt.setString(4, appointment.getAppointmentTime());
                    pstmt.setString(5, appointment.getDescription());
                    pstmt.setString(6, appointment.getStatus());
                    pstmt.addBatch();
                }
                
                pstmt.executeBatch();
                connection.commit();
                return true;
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
            System.err.println("Error saving appointments: " + e.getMessage());
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

    // Get filepath
    public String getCurrentDatabasePath() {
        return currentDatabasePath;
    }

   // Check if connected to the database
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    // Close database connections (Note remember this to add this)
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}
