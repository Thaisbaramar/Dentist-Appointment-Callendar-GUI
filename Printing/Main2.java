package Printing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Everything.UserType;

public class Main2 {

    public static void main(String[] args) {
        // Read appointments from scheduledAppointments.txt
        Map<String, Set<String>> scheduledAppointments = readScheduledAppointmentsFromFile("scheduledAppointments.txt");

        // Print scheduled appointments
        PrintScheduledAppointments.printScheduledAppointments(scheduledAppointments);

        // Read user information from SavedInfo.txt
        UserManager userManager = readUserInformationFromFile("SavedInfo.txt");

        // Print user information
        PrintScheduledAppointments.printUserInformation(userManager);
    }

    private static Map<String, Set<String>> readScheduledAppointmentsFromFile(String filename) {
        Map<String, Set<String>> scheduledAppointments = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String timeSlot = parts[0];
                String name = parts[1];
                scheduledAppointments.computeIfAbsent(timeSlot, k -> new HashSet<>()).add(name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scheduledAppointments;
    }

    private static UserManager readUserInformationFromFile(String filename) {
        UserManager userManager = new UserManager();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                String email = parts[0];
                String password = parts[1];
                UserType userType = UserType.valueOf(parts[2]);
                userManager.registerUser(email, password, userType);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userManager;
    }
}
