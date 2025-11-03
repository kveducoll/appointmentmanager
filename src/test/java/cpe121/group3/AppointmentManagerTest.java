package cpe121.group3;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

public class AppointmentManagerTest {

    private AppointmentManager mgr;
    private File tmpFile;

    @BeforeEach
    void setup() {
        // Reset singleton by reflection since AppointmentManager is a singleton in production
        try {
            java.lang.reflect.Field instance = AppointmentManager.class.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (Exception e) {
            // ignore
        }
        mgr = AppointmentManager.getInstance();
    }

    @AfterEach
    void cleanup() {
        mgr.clearAllAppointments();
        if (tmpFile != null && tmpFile.exists()) tmpFile.delete();
    }

    @Test
    void addUpdateDeleteFlow() {
        Appointment a = new Appointment("X","Y","2025-08-22","12:00","D","Scheduled");
        mgr.addAppointment(a);
        assertEquals(1, mgr.getAppointments().size());
        assertTrue(mgr.hasUnsavedChanges());

        Appointment a2 = new Appointment("X2","Y","2025-08-22","12:00","D","Scheduled");
        mgr.updateAppointment(0, a2);
        assertEquals("X2", mgr.getAppointments().get(0).getTitle());

        mgr.deleteAppointment(a2);
        assertEquals(0, mgr.getAppointments().size());
    }

    @Test
    void saveAndLoadToFile() {
        try {
            tmpFile = File.createTempFile("am-test", ".apf");
            String path = tmpFile.getAbsolutePath();
            tmpFile.delete();

            Appointment a = new Appointment("S","P","2025-08-22","07:00","D","Scheduled");
            mgr.addAppointment(a);

            assertTrue(mgr.saveToFile(path));
            assertTrue(mgr.isFileOpen());

            // Create a new manager instance to load
            java.lang.reflect.Field instance;
            instance = AppointmentManager.class.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(null, null);
            AppointmentManager mgr2 = AppointmentManager.getInstance();

            assertTrue(mgr2.loadFromFile(path));
            assertEquals(1, mgr2.getAppointments().size());
            assertEquals("S", mgr2.getAppointments().get(0).getTitle());

        } catch (Exception e) {
            fail(e);
        }
    }
}
