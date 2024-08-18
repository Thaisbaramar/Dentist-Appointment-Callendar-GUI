package Everything;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.util.*;

public class CalendarPage extends JFrame {
    private String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private JLabel monthLabel;
    private YearMonth currentYearMonth;
    private Map<LocalDate, Boolean> fullDaysMap;
    private UserType currentUser;
    private JPanel calendarPanel;
    private Map<String, Set<String>> scheduledAppointments;

    public CalendarPage(UserType currentUser) {
        this.currentUser = currentUser;
        setTitle("Calendar Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        scheduledAppointments = new HashMap<>();

        fullDaysMap = new HashMap<>();
        currentYearMonth = YearMonth.now();

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Add the month label to display the current month
        monthLabel = new JLabel(getCurrentMonthYear(), SwingConstants.CENTER);
        monthLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font size for the month label
        mainPanel.add(monthLabel, BorderLayout.NORTH);

        // Create a panel for the weekdays
        JPanel weekDaysPanel = new JPanel(new GridLayout(1, 7));
        createWeekDaysLabels(weekDaysPanel);
        mainPanel.add(weekDaysPanel, BorderLayout.CENTER);

        // Create a panel for the calendar days
        calendarPanel = new JPanel(new GridLayout(6, 7));
        createCalendarButtons(calendarPanel);
        mainPanel.add(calendarPanel, BorderLayout.CENTER);

        // Create navigation buttons to switch to the previous or next month
        JPanel navigationPanel = createNavigationPanel();
        mainPanel.add(navigationPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void openAppointmentList() {
        AppointmentListFrame appointmentListFrame = new AppointmentListFrame(currentUser, scheduledAppointments);
        appointmentListFrame.setVisible(true);
    }

    private void createWeekDaysLabels(JPanel weekDaysPanel) {
        for (String day : daysOfWeek) {
            JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
            dayLabel.setForeground(Color.WHITE);
            dayLabel.setBackground(Color.BLACK);
            dayLabel.setOpaque(true);
            weekDaysPanel.add(dayLabel);
        }
    }

    private void createCalendarButtons(JPanel calendarPanel) {
        calendarPanel.removeAll();
        int daysInMonth = currentYearMonth.lengthOfMonth();
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
        int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7;

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                JButton dayButton = new JButton();
                int dayOfMonth = (i * 7 + j + 1) - dayOfWeek;
                if (dayOfMonth > 0 && dayOfMonth <= daysInMonth) {
                    dayButton.setText(Integer.toString(dayOfMonth));
                    LocalDate buttonDate = currentYearMonth.atDay(dayOfMonth);
                    setButtonStyle(dayButton, buttonDate, today);
                    dayButton.addActionListener(e -> openTimeSlotsPage(dayOfMonth));
                } else {
                    dayButton.setEnabled(false);
                }
                calendarPanel.add(dayButton);
            }
        }
    }

    private void setButtonStyle(JButton dayButton, LocalDate buttonDate, LocalDate today) {
        if (buttonDate.isBefore(today)) {
            dayButton.setBackground(new Color(255, 182, 193));
            dayButton.setForeground(Color.RED);
            dayButton.setEnabled(false);
        } else {
            dayButton.setForeground(Color.BLACK);
            dayButton.setBackground(Color.WHITE);
            if (fullDaysMap.containsKey(buttonDate) && fullDaysMap.get(buttonDate)) {
                dayButton.setForeground(Color.RED);
            }
        }
    }

    private String getCurrentMonthYear() {
        return currentYearMonth.getMonth().toString() + " " + currentYearMonth.getYear();
    }

    private void openTimeSlotsPage(int selectedDay) {
        TimeSlots timeSlots = new TimeSlots(currentUser, selectedDay, currentYearMonth.getMonthValue(), currentYearMonth.getYear(), this);
        setVisible(false);
        timeSlots.setVisible(true);
    }


    private JPanel createNavigationPanel() {
        JPanel navigationPanel = new JPanel(new FlowLayout());
        JButton prevMonthButton = new JButton("Previous Month");
        prevMonthButton.addActionListener(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            monthLabel.setText(getCurrentMonthYear());
            createCalendarButtons(calendarPanel);
        });
        navigationPanel.add(prevMonthButton);

        JButton nextMonthButton = new JButton("Next Month");
        nextMonthButton.addActionListener(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            monthLabel.setText(getCurrentMonthYear());
            createCalendarButtons(calendarPanel);
        });
        navigationPanel.add(nextMonthButton);

        JButton viewAppointmentsButton = new JButton("View Appointments");
        viewAppointmentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAppointmentList();
            }
        });
        navigationPanel.add(viewAppointmentsButton);

        return navigationPanel;
    }
    
    public void updateCalendarButton(int selectedDay, String timeSlot) {
        for (Component component : calendarPanel.getComponents()) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                if (button.getText().equals(Integer.toString(selectedDay))) {
                    setButtonStyle(button, currentYearMonth.atDay(selectedDay), LocalDate.now());
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserType userType = UserType.CLIENT; // Or UserType.DENTIST, UserType.STAFF, depending on your requirement
            CalendarPage calendarPage = new CalendarPage(userType);
        });
    }
}