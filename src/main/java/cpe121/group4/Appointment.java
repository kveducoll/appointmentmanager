package cpe121.group4;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model class representing a general appointment
 */
public class Appointment {
    private final StringProperty title;
    private final StringProperty participant;
    private final StringProperty appointmentDate;
    private final StringProperty appointmentTime;
    private final StringProperty description;
    private final StringProperty status;

    public Appointment() {
        this("", "", "", "", "", "Scheduled");
    }

    public Appointment(String title, String participant, String appointmentDate, 
                      String appointmentTime, String description, String status) {
        this.title = new SimpleStringProperty(title);
        this.participant = new SimpleStringProperty(participant);
        this.appointmentDate = new SimpleStringProperty(appointmentDate);
        this.appointmentTime = new SimpleStringProperty(appointmentTime);
        this.description = new SimpleStringProperty(description);
        this.status = new SimpleStringProperty(status);
    }

    // Title
    public StringProperty titleProperty() { return title; }
    public String getTitle() { return title.get(); }
    public void setTitle(String title) { this.title.set(title); }

    // Participant
    public StringProperty participantProperty() { return participant; }
    public String getParticipant() { return participant.get(); }
    public void setParticipant(String participant) { this.participant.set(participant); }

    // Appointment Date
    public StringProperty appointmentDateProperty() { return appointmentDate; }
    public String getAppointmentDate() { return appointmentDate.get(); }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate.set(appointmentDate); }

    // Appointment Time
    public StringProperty appointmentTimeProperty() { return appointmentTime; }
    public String getAppointmentTime() { return appointmentTime.get(); }
    public void setAppointmentTime(String appointmentTime) { this.appointmentTime.set(appointmentTime); }

    // Description
    public StringProperty descriptionProperty() { return description; }
    public String getDescription() { return description.get(); }
    public void setDescription(String description) { this.description.set(description); }

    // Status
    public StringProperty statusProperty() { return status; }
    public String getStatus() { return status.get(); }
    public void setStatus(String status) { this.status.set(status); }

    @Override
    public String toString() {
        return String.format("Appointment{title='%s', participant='%s', date='%s', time='%s', status='%s'}", 
                           getTitle(), getParticipant(), getAppointmentDate(), getAppointmentTime(), getStatus());
    }
}
