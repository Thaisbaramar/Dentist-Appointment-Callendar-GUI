package Everything;
import Printing.PrintScheduledAppointments;
import Printing.UserManager;

import javax.swing.*;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import Printing.PrintScheduledAppointments;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::initializeGUI);
    }

    private static void initializeGUI() {
        ExecutorService executor = Executors.newCachedThreadPool();

        try {
            // Create an instance of UserManager
            UserManager userManager = new UserManager();

            // Register staff 
            userManager.createStaff("staff1@", "admin1");
            userManager.createStaff("staff2@", "admin2");
            userManager.createStaff("staff3@", "admin3");
            // Register Dentist
            userManager.registerUser("dentist1@example.com", "teeth1", UserType.DENTIST);

            // Create an instance of LoginPage with UserManager
            LoginPage loginPage = new LoginPage(userManager);
            loginPage.setVisible(true);

            // Submit task to handle login process
            executor.submit(() -> {
                try {
                    // Wait for the login process to complete before proceeding
                    UserType currentUser = loginPage.waitForLogin();

                    // Once logged in, proceed to display calendar and time slots
                    if (currentUser != null) {
                        SwingUtilities.invokeLater(() -> {
                            CalendarPage calendarPage = new CalendarPage(currentUser);
                            calendarPage.setVisible(true);
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shutdown the executor when no more tasks need to be submitted
            executor.shutdown();
        }
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