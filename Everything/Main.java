package Everything;
import Printing.PrintScheduledAppointments;
import Printing.UserManager;

import javax.swing.*;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::initializeGUI);
    }

    private static void initializeGUI() {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // Load accounts and appointments from the files off the UI thread,
        // then show the login window on the UI thread.
        executor.submit(() -> {
            try {
                UserManager userManager = new UserManager();
                registerSampleAccounts(userManager);
                AppointmentStore store = new AppointmentStore();

                SwingUtilities.invokeLater(() -> {
                    LoginPage loginPage = new LoginPage(userManager, store);
                    loginPage.setVisible(true);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        executor.shutdown();
    }

    // Accounts that exist from the first run, one for each access level
    private static void registerSampleAccounts(UserManager userManager) {
        userManager.ensureUser("staff1@", "admin1", UserType.STAFF, "Staff One");
        userManager.ensureUser("staff2@", "admin2", UserType.STAFF, "Staff Two");
        userManager.ensureUser("staff3@", "admin3", UserType.STAFF, "Staff Three");
        userManager.ensureUser("dentist1@example.com", "teeth1", UserType.DENTIST, "Dr. Dentist");
        userManager.ensureUser("secretary1@example.com", "desk1", UserType.SECRETARY, "Front Desk");
        userManager.ensureUser("patient1@example.com", "patient1", UserType.CLIENT, "Test Patient");
    }

    // Method to display the schedule appointment frame
    public static void displayScheduleAppointmentFrame(Map<String, Set<String>> scheduledAppointments, JFrame previousPage, String timeSlot, int selectedDay) {
        SwingUtilities.invokeLater(() -> {
            ScheduledAppointmentFrame frame = new ScheduledAppointmentFrame(scheduledAppointments, previousPage, timeSlot, selectedDay);
            frame.setVisible(true);
        });
    }

    // Method to print scheduled appointments
    public static void printScheduledAppointments(Map<String, Set<String>> scheduledAppointments) {
        PrintScheduledAppointments.printScheduledAppointments(scheduledAppointments);
    }
}
