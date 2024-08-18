package Everything;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class AppointmentListFrame extends JFrame {
    private Map<String, Set<String>> scheduledAppointments;
    private UserType currentUser;

    public AppointmentListFrame(UserType currentUser, Map<String, Set<String>> scheduledAppointments) {
        this.currentUser = currentUser;
        this.scheduledAppointments = scheduledAppointments;
        initComponents();
    }

    private void initComponents() {
        setTitle("Scheduled Appointments");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel appointmentsPanel = new JPanel();
        appointmentsPanel.setLayout(new BoxLayout(appointmentsPanel, BoxLayout.Y_AXIS));

        if (scheduledAppointments != null && !scheduledAppointments.isEmpty()) {
            // Populate the appointmentsPanel with the scheduled appointments
            for (Map.Entry<String, Set<String>> entry : scheduledAppointments.entrySet()) {
                String timeSlot = entry.getKey();
                Set<String> users = entry.getValue();
                String appointmentsText = formatAppointmentsText(timeSlot, users);
                JLabel appointmentsLabel = new JLabel(appointmentsText);
                appointmentsPanel.add(appointmentsLabel);
            }
        } else {
            JLabel noAppointmentsLabel = new JLabel("No appointments scheduled");
            noAppointmentsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            appointmentsPanel.add(Box.createVerticalGlue());
            appointmentsPanel.add(noAppointmentsLabel);
            appointmentsPanel.add(Box.createVerticalGlue());
        }

        JScrollPane scrollPane = new JScrollPane(appointmentsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(scrollPane, BorderLayout.CENTER);

        JButton returnButton = new JButton("Return to Calendar");
        returnButton.addActionListener(e -> {
            dispose();
            new CalendarPage(currentUser).setVisible(true);
        });
        contentPane.add(returnButton, BorderLayout.SOUTH);

        setContentPane(contentPane);
        setVisible(true);
    }

    private String formatAppointmentsText(String timeSlot, Set<String> users) {
        StringBuilder appointmentsText = new StringBuilder("<html><b>" + timeSlot + ":</b> ");
        for (String user : users) {
            appointmentsText.append(user).append(", ");
        }
        if (!users.isEmpty()) {
            appointmentsText.delete(appointmentsText.length() - 2, appointmentsText.length()); // Remove the last comma
        }
        appointmentsText.append("</html>");
        return appointmentsText.toString();
    }
}

