 package Everything;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.*;

public class TimeSlots extends JFrame {
    private UserType currentUser;
    private int selectedDay;
    private int selectedMonth;
    private int selectedYear;
    private Map<String, Set<String>> scheduledAppointments;
    private CalendarPage calendarPage;
    private String filename = "scheduledAppointments.ser";

    public TimeSlots(UserType user, int selectedDay, int selectedMonth, int selectedYear, CalendarPage calendarPage) {
        this.currentUser = user;
        this.selectedDay = selectedDay;
        this.selectedMonth = selectedMonth;
        this.selectedYear = selectedYear;
        this.calendarPage = calendarPage;
        this.scheduledAppointments = loadScheduledAppointmentsFromFile();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(250, 450);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel datePanel = createDatePanel();
        JPanel slotsPanel = createSlotsPanel();
        JScrollPane scrollPane = new JScrollPane(slotsPanel);

        add(datePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(createReturnButton(), BorderLayout.SOUTH);
    }

    private JPanel createDatePanel() {
        JPanel datePanel = new JPanel();
        LocalDate currentDate = LocalDate.of(selectedYear, selectedMonth, selectedDay);
        JLabel dateLabel = new JLabel("Date: " + currentDate);
        datePanel.add(dateLabel);
        return datePanel;
    }

    private JPanel createSlotsPanel() {
        JPanel slotsPanel = new JPanel(new GridLayout(0, 1, 5, 5)); // Vertical layout
        for (int i = 8; i <= 17; i++) {
            String time = String.format("%02d:00", i);
            TimeSlotPanel timeSlotPanel = new TimeSlotPanel(time);
            slotsPanel.add(timeSlotPanel);
        }
        return slotsPanel;
    }

    private JButton createReturnButton() {
        JButton returnButton = new JButton("Return");
        returnButton.addActionListener(e -> {
            dispose();
            if (calendarPage != null) {
                calendarPage.setVisible(true); // Show the CalendarPage again if available
            }
        });
        return returnButton;
    }

    public class TimeSlotPanel extends JPanel {
        JButton timeButton;

        public TimeSlotPanel(String time) {
            setLayout(new FlowLayout(FlowLayout.CENTER));
            timeButton = new JButton(time);
            timeButton.setPreferredSize(new Dimension(80, 30));
            timeButton.setBackground(Color.WHITE);
            timeButton.addActionListener(e -> openScheduleManaging(time));
            add(timeButton);
            updateSlotAvailability(time);
        }

        private void updateSlotAvailability(String time) {
            if (isSlotOccupied(time)) {
                timeButton.setForeground(Color.GRAY);
                timeButton.setEnabled(false);
            }
        }

        private void openScheduleManaging(String time) {
            ScheduleManaging scheduleManaging = new ScheduleManaging(time, scheduledAppointments);
            scheduleManaging.setVisible(true);
        }
    }

    private boolean isSlotOccupied(String time) {
        String key = getDateString(selectedDay, selectedMonth, selectedYear);
        return scheduledAppointments.containsKey(key) && scheduledAppointments.get(key).contains(time);
    }

    private String getDateString(int day, int month, int year) {
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    private Map<String, Set<String>> loadScheduledAppointmentsFromFile() {
        Map<String, Set<String>> loadedAppointments = new HashMap<>();
        // Implement loading from file
        return loadedAppointments;
    }

    public static void main(String[] args) {
        // Example usage
        UserType userType = UserType.CLIENT;
        TimeSlots timeSlots = new TimeSlots(userType, 1, 1, 2024, null); // null as calendarPage, adjust accordingly
        timeSlots.setVisible(true);
    }
}