package Everything;
import java.io.*;
import java.util.*;

public class TimeSlotLoader {
    private String filename;

    public TimeSlotLoader(String filename) {
        this.filename = filename;
    }

    public Map<String, Set<String>> loadScheduledAppointmentsFromFile() {
        Map<String, Set<String>> loadedAppointments = new HashMap<>();
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("Scheduled appointments file not found. Initializing with an empty map.");
            return loadedAppointments;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            loadedAppointments = (Map<String, Set<String>>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading scheduled appointments: " + e.getMessage());
        }
        return loadedAppointments;
    }

    public void saveScheduledAppointmentsToFile(Map<String, Set<String>> scheduledAppointments) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(scheduledAppointments);
        } catch (IOException e) {
            System.out.println("Error saving scheduled appointments: " + e.getMessage());
        }
    }
}