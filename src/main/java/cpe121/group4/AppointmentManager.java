package cpe121.group4;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Appointment data manager, WALA PANI DATA BASE
 */
public class AppointmentManager {
    private static AppointmentManager instance;
    private ObservableList<Appointment> appointments;

    private AppointmentManager() {
        appointments = FXCollections.observableArrayList();
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
    }

    public void updateAppointment(int index, Appointment appointment) {
        if (index >= 0 && index < appointments.size()) {
            appointments.set(index, appointment);
        }
    }

    public void deleteAppointment(Appointment appointment) {
        appointments.remove(appointment);
    }

    public void deleteAppointment(int index) {
        if (index >= 0 && index < appointments.size()) {
            appointments.remove(index);
        }
    }

    public int getAppointmentIndex(Appointment appointment) {
        return appointments.indexOf(appointment);
    }

    public void clearAllAppointments() {
        appointments.clear();
    }
}
