package Everything;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.util.*;

import Printing.User;
import Printing.UserManager;

public class CalendarPage extends JFrame {
    private String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private JLabel monthLabel;
    private YearMonth currentYearMonth;
    private final User currentUser;
    private final UserManager userManager;
    private final AppointmentStore store;
    private JPanel calendarPanel;
    private Map<String, Set<String>> scheduledAppointments;

    public CalendarPage(User currentUser, UserManager userManager, AppointmentStore store) {
        this.currentUser = currentUser;
        this.userManager = userManager;
        this.store = store;
        setTitle("Calendar Page - " + currentUser.getName() + " (" + currentUser.getUserType().getLabel() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        scheduledAppointments = new HashMap<>();

        currentYearMonth = YearMonth.now();

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Add the month label to display the current month
        monthLabel = new JLabel(getCurrentMonthYear(), SwingConstants.CENTER);
        monthLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font size for the month label
        mainPanel.add(monthLabel, BorderLayout.NORTH);

        // Weekday header on top of the day grid (the original put both in CENTER, so the header was lost)
        JPanel weekDaysPanel = new JPanel(new GridLayout(1, 7));
        createWeekDaysLabels(weekDaysPanel);
        calendarPanel = new JPanel(new GridLayout(6, 7));
        createCalendarButtons(calendarPanel);
        JPanel gridPanel = new JPanel(new BorderLayout());
        gridPanel.add(weekDaysPanel, BorderLayout.NORTH);
        gridPanel.add(calendarPanel, BorderLayout.CENTER);
        mainPanel.add(gridPanel, BorderLayout.CENTER);

        // Create navigation buttons to switch to the previous or next month
        JPanel navigationPanel = createNavigationPanel();
        mainPanel.add(navigationPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void openAppointmentList() {
        AppointmentListFrame appointmentListFrame = new AppointmentListFrame(currentUser, store, this);
        setVisible(false);
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
            if (store.isFull(buttonDate)) {
                dayButton.setForeground(Color.RED);
            } else if (store.hasAppointmentsVisibleTo(currentUser, buttonDate)) {
                dayButton.setBackground(new Color(200, 225, 255)); // there is something you can see on this day
            }
        }
    }

    private String getCurrentMonthYear() {
        return currentYearMonth.getMonth().toString() + " " + currentYearMonth.getYear();
    }

    private void openTimeSlotsPage(int selectedDay) {
        TimeSlots timeSlots = new TimeSlots(currentUser, userManager, store, selectedDay, currentYearMonth.getMonthValue(), currentYearMonth.getYear(), this);
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

        // Only people who may register patients / manage accounts see these buttons
        if (currentUser.hasPermission(Permission.MANAGE_ACCOUNTS)) {
            JButton manageButton = new JButton("Manage Accounts");
            manageButton.addActionListener(e -> new RegistrationPage(userManager, currentUser).setVisible(true));
            navigationPanel.add(manageButton);
        } else if (currentUser.hasPermission(Permission.REGISTER_PATIENTS)) {
            JButton registerButton = new JButton("Register Patient");
            registerButton.addActionListener(e -> new RegistrationPage(userManager, currentUser).setVisible(true));
            navigationPanel.add(registerButton);
        }

        // Patients get a button to read what the dentist or front desk told them
        if (currentUser.hasPermission(Permission.VIEW_OWN_APPOINTMENTS)) {
            JButton notificationsButton = new JButton("Notifications");
            notificationsButton.addActionListener(e -> showNotifications(false));
            navigationPanel.add(notificationsButton);
        }

        JButton logoutButton = new JButton("Log out");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginPage(userManager, store).setVisible(true);
        });
        navigationPanel.add(logoutButton);

        return navigationPanel;
    }
    
    public void updateCalendarButton(int selectedDay, String timeSlot) {
        refresh();
    }

    /** Shows the notifications for the logged-in user: only the unread ones, or all of them. */
    public void showNotifications(boolean onlyUnread) {
        java.util.List<Printing.Notification> list = onlyUnread ? store.unreadFor(currentUser) : store.notificationsFor(currentUser);
        if (list.isEmpty()) {
            if (!onlyUnread) {
                JOptionPane.showMessageDialog(this, "No notifications.", "Notifications", JOptionPane.INFORMATION_MESSAGE);
            }
            return;
        }
        StringBuilder text = new StringBuilder();
        for (Printing.Notification n : list) {
            text.append(n.isRead() ? "" : "* ").append(n.getTime().toLocalDate()).append(" ")
                .append(String.format("%02d:%02d", n.getTime().getHour(), n.getTime().getMinute()))
                .append(" - ").append(n.getMessage()).append("\n\n");
        }
        JTextArea area = new JTextArea(text.toString(), 10, 48);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Notifications (* = new)", JOptionPane.INFORMATION_MESSAGE);
        store.markAllRead(currentUser);
    }

    /** Redraws the day buttons from the current appointment data. */
    public void refresh() {
        createCalendarButtons(calendarPanel);
        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserManager userManager = new UserManager();
            userManager.ensureUser("patient1@example.com", "patient1", UserType.CLIENT, "Test Patient");
            CalendarPage calendarPage = new CalendarPage(userManager.getUserByEmail("patient1@example.com"), userManager, new AppointmentStore());
            calendarPage.setVisible(true);
        });
    }
}
