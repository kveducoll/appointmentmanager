package cpe121.group3;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AppointmentTest {

    @Test
    void propertiesAndAccessorsWork() {
        Appointment a = new Appointment("Meeting","Alice","2025-08-22","10:00","Discuss", "Scheduled");
        assertEquals("Meeting", a.getTitle());
        assertEquals("Alice", a.getParticipant());
        assertEquals("2025-08-22", a.getAppointmentDate());
        assertEquals("10:00", a.getAppointmentTime());
        assertEquals("Discuss", a.getDescription());
        assertEquals("Scheduled", a.getStatus());

        a.setTitle("New");
        a.setParticipant("Bob");
        a.setAppointmentDate("2025-09-01");
        a.setAppointmentTime("11:30");
        a.setDescription("Notes");
        a.setStatus("Completed");

        assertEquals("New", a.getTitle());
        assertEquals("Bob", a.getParticipant());
        assertEquals("2025-09-01", a.getAppointmentDate());
        assertEquals("11:30", a.getAppointmentTime());
        assertEquals("Notes", a.getDescription());
        assertEquals("Completed", a.getStatus());
    }

    @Test
    void defaultConstructorSetsScheduledStatus() {
        Appointment a = new Appointment();
        assertEquals("Scheduled", a.getStatus());
    }
}
