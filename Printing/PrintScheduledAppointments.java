package Printing;
import java.util.Map;
import java.util.Set;

public class PrintScheduledAppointments {

    public static void printScheduledAppointments(Map<String, Set<String>> scheduledAppointments) {
        System.out.println("Scheduled Appointments:");
        for (Map.Entry<String, Set<String>> entry : scheduledAppointments.entrySet()) {
            String timeSlot = entry.getKey();
            Set<String> appointments = entry.getValue();
            System.out.println("Time Slot: " + timeSlot);
            System.out.println("Appointments:");
            for (String appointment : appointments) {
                System.out.println("- " + appointment);
            }
            System.out.println();
        }
    }

    public static void printUserInformation(UserManager userManager) {
        System.out.println("User Information:");
        for (User user : userManager.getUsers()) {
            System.out.println("Email: " + user.getEmail());
            System.out.println("Password: " + user.getPassword());
            System.out.println("User Type: " + user.getUserType());
            System.out.println();
        }
    }
}
