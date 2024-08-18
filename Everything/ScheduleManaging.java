package Everything;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class ScheduleManaging extends JFrame {
    private String timeSlot;
    private Map<String, Set<String>> scheduledAppointments;

    public ScheduleManaging(String timeSlot, Map<String, Set<String>> scheduledAppointments) {
        this.timeSlot = timeSlot;
        this.scheduledAppointments = scheduledAppointments;
        initComponents();
    }

    private void initComponents() {
        setTitle("Schedule Managing");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel(new BorderLayout());

        JLabel timeSlotLabel = new JLabel("Selected Time Slot: " + timeSlot);
        contentPane.add(timeSlotLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridLayout(2, 1));
        JTextField nameField = new JTextField();
        inputPanel.add(new JLabel("Enter your name:"));
        inputPanel.add(nameField);
        contentPane.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton scheduleButton = new JButton("Schedule Appointment");
        scheduleButton.addActionListener(e -> scheduleAppointment(nameField.getText()));
        buttonPanel.add(scheduleButton);

        JButton cancelButton = new JButton("Cancel Appointment"); // Corrected button label
        cancelButton.addActionListener(e -> cancelAppointment(nameField.getText()));
        buttonPanel.add(cancelButton);

        JButton returnButton = new JButton("Return");
        returnButton.addActionListener(e -> dispose());
        buttonPanel.add(returnButton);

        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(contentPane);
    }

    private void scheduleAppointment(String name) {
        Set<String> appointments = scheduledAppointments.getOrDefault(timeSlot, new HashSet<>());
        appointments.add(name);
        scheduledAppointments.put(timeSlot, appointments);
        JOptionPane.showMessageDialog(this, "Appointment scheduled for " + timeSlot + " with " + name);
    }

    private void cancelAppointment(String name) {
        if (scheduledAppointments.containsKey(timeSlot)) {
            Set<String> appointments = scheduledAppointments.get(timeSlot);
            if (appointments.contains(name)) {
                appointments.remove(name);
                JOptionPane.showMessageDialog(this, "Appointment canceled for " + timeSlot + " with " + name);
            } else {
                JOptionPane.showMessageDialog(this, "No appointment found for " + name + " at " + timeSlot);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No appointment scheduled for " + timeSlot);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Map<String, Set<String>> scheduledAppointments = new HashMap<>();
            ScheduleManaging scheduleManaging = new ScheduleManaging("10:00 AM", scheduledAppointments);
            scheduleManaging.setVisible(true);
        });
    }
}