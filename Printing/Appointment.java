package Printing;

import java.time.LocalDate;

public class Appointment {
    public enum Status { PENDING, CONFIRMED }

    private Status status = Status.PENDING;
    private String timeSlot;
    private String name;
    private int selectedDay;
    private LocalDate date;
    private String patientEmail;

    // Original constructor (kept so old code still compiles)
    public Appointment(String timeSlot, String name, int selectedDay) {
        this.timeSlot = timeSlot;
        this.name = name;
        this.selectedDay = selectedDay;
    }

    // An appointment now belongs to a date and to a patient account
    public Appointment(LocalDate date, String timeSlot, String patientEmail, String name) {
        this(timeSlot, name, date.getDayOfMonth());
        this.date = date;
        this.patientEmail = patientEmail;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSelectedDay() {
        return selectedDay;
    }

    public void setSelectedDay(int selectedDay) {
        this.selectedDay = selectedDay;
    }

    public LocalDate getDate() {
        return date;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public boolean belongsTo(User user) {
        return user != null && patientEmail != null && patientEmail.equalsIgnoreCase(user.getEmail());
    }

    public void printAppointmentInfo() {
        System.out.println("Date: " + date);
        System.out.println("Time Slot: " + timeSlot);
        System.out.println("Name: " + name);
        System.out.println("Selected Day: " + selectedDay);
    }
}
