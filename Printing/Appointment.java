package Printing;

public class Appointment {
    private String timeSlot;
    private String name;
    private int selectedDay;

    public Appointment(String timeSlot, String name, int selectedDay) {
        this.timeSlot = timeSlot;
        this.name = name;
        this.selectedDay = selectedDay;
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

    public void printAppointmentInfo() {
        System.out.println("Time Slot: " + timeSlot);
        System.out.println("Name: " + name);
        System.out.println("Selected Day: " + selectedDay);
    }
}