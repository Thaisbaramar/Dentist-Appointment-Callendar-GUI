package Everything;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class ScheduledAppointmentFrame extends JFrame {
    private int selectedDay;
    private String timeSlot;
    private Map<String, Set<String>> scheduledAppointments;
    private JFrame previousPage;
    private final String FILE_NAME = "scheduledAppointments.txt";

    public ScheduledAppointmentFrame(Map<String, Set<String>> scheduledAppointments, JFrame previousPage, String timeSlot, int selectedDay) {
        this.scheduledAppointments = scheduledAppointments;
        this.previousPage = previousPage;
        this.timeSlot = timeSlot;
        this.selectedDay = selectedDay; // Assign the selected day
        initComponents();
    }

    private void initComponents() {
        setTitle("Schedule Appointment");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel(new BorderLayout());

        JLabel dayLabel = new JLabel("Selected Day: " + selectedDay);
        JLabel timeSlotLabel = new JLabel("Selected Time Slot: " + timeSlot);
        contentPane.add(dayLabel, BorderLayout.NORTH);
        contentPane.add(timeSlotLabel, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(2, 1));
        JTextField nameField = new JTextField();
        inputPanel.add(new JLabel("Enter your name:"));
        inputPanel.add(nameField);
        contentPane.add(inputPanel, BorderLayout.CENTER);

        // Display existing appointments for the selected time slot
        JPanel existingAppointmentsPanel = new JPanel(new GridLayout(0, 1));
        Set<String> existingAppointments = scheduledAppointments.getOrDefault(timeSlot, new HashSet<>());
        for (String appointment : existingAppointments) {
            existingAppointmentsPanel.add(new JLabel(appointment));
        }
        contentPane.add(existingAppointmentsPanel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton scheduleButton = new JButton("Schedule");
        scheduleButton.setEnabled(existingAppointments.isEmpty()); // Disable button if time slot is already scheduled
        scheduleButton.addActionListener(e -> {
            String name = nameField.getText();
            if (!name.isEmpty()) {
                scheduleAppointment(name);
                saveAppointmentsToFile();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Please enter your name.");
            }
        });
        buttonPanel.add(scheduleButton);

        JButton cancelButton = new JButton("Cancel Appointment");
        cancelButton.addActionListener(e -> {
            cancelAppointment();
            dispose();
        });
        buttonPanel.add(cancelButton);

        JButton returnButton = new JButton("Return to Calendar");
        returnButton.addActionListener(e -> {
            dispose();
            previousPage.setVisible(true);
        });
        buttonPanel.add(returnButton);

        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(contentPane);
        setVisible(true);
    }

    private void scheduleAppointment(String name) {
        Set<String> appointments = scheduledAppointments.getOrDefault(timeSlot, new HashSet<>());
        appointments.add(name);
        scheduledAppointments.put(timeSlot, appointments);
        // Update the time slot appearance in the calendar page
        if (previousPage instanceof CalendarPage) {
            ((CalendarPage) previousPage).updateCalendarButton(selectedDay, timeSlot);
        }
    }

    private void cancelAppointment() {
        scheduledAppointments.remove(timeSlot);
        // Update the time slot appearance in the calendar page
        if (previousPage instanceof CalendarPage) {
            ((CalendarPage) previousPage).updateCalendarButton(selectedDay, timeSlot);
        }
    }

    private void saveAppointmentsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Map.Entry<String, Set<String>> entry : scheduledAppointments.entrySet()) {
                String timeSlot = entry.getKey();
                Set<String> appointments = entry.getValue();
                for (String name : appointments) {
                    writer.println(timeSlot + "," + name + "," + selectedDay); // Save day along with time slot and name
                }
            }
            System.out.println("Appointments saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving appointments: " + e.getMessage());
        }
    }
}