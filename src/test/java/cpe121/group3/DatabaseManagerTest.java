package cpe121.group3;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseManagerTest {

    private DatabaseManager db = new DatabaseManager();
    private File tmpFile;

    @AfterEach
    void cleanup() {
        if (db.isConnected()) db.closeConnection();
        if (tmpFile != null && tmpFile.exists()) tmpFile.delete();
    }

    @Test
    void connectCreatesFileAndTable() {
        try {
            tmpFile = File.createTempFile("test-db", ".apf");
            String path = tmpFile.getAbsolutePath();
            // delete it to let connect create it
            tmpFile.delete();
            boolean ok = db.connectToDatabase(path);
            assertTrue(ok);
            assertTrue(db.isConnected());
            assertNotNull(db.getCurrentDatabasePath());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void saveLoadAndDeleteAppointment() {
        try {
            tmpFile = File.createTempFile("test-db", ".apf");
            String path = tmpFile.getAbsolutePath();
            tmpFile.delete();
            assertTrue(db.connectToDatabase(path));

            Appointment a = new Appointment("T","P","2025-08-22","09:00","D","Scheduled");
            assertTrue(db.saveAppointment(a));

            List<Appointment> loaded = db.loadAppointments();
            assertEquals(1, loaded.size());
            Appointment l = loaded.get(0);
            assertEquals("T", l.getTitle());

            // Update appointment
            Appointment newA = new Appointment("T2","P","2025-08-22","09:00","D","Completed");
            assertTrue(db.updateAppointment(a, newA));

            List<Appointment> afterUpdate = db.loadAppointments();
            assertEquals(1, afterUpdate.size());
            assertEquals("T2", afterUpdate.get(0).getTitle());

            // Delete
            assertTrue(db.deleteAppointment(newA));
            List<Appointment> afterDelete = db.loadAppointments();
            assertEquals(0, afterDelete.size());

        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void saveAllAppointmentsBulk() {
        try {
            tmpFile = File.createTempFile("test-db", ".apf");
            String path = tmpFile.getAbsolutePath();
            tmpFile.delete();
            assertTrue(db.connectToDatabase(path));

            Appointment a1 = new Appointment("A1","P1","2025-08-22","08:00","D","Scheduled");
            Appointment a2 = new Appointment("A2","P2","2025-08-22","09:00","D","Scheduled");
            assertTrue(db.saveAllAppointments(List.of(a1, a2)));

            List<Appointment> loaded = db.loadAppointments();
            assertEquals(2, loaded.size());

            assertTrue(db.clearAllAppointments());
            List<Appointment> afterClear = db.loadAppointments();
            assertEquals(0, afterClear.size());

        } catch (Exception e) {
            fail(e);
        }
    }
}
