package Everything;

import javax.swing.*;

import Printing.Appointment;
import Printing.User;
import Printing.UserManager;

import java.awt.*;
import java.time.LocalDate;
import java.util.Optional;

public class TimeSlots extends JFrame {
    private final User currentUser;
    private final UserManager userManager;
    private final AppointmentStore store;
    private final LocalDate date;
    private final int selectedDay;
    private final CalendarPage calendarPage;
    private JPanel slotsPanel;

    public TimeSlots(User user, UserManager userManager, AppointmentStore store,
                     int selectedDay, int selectedMonth, int selectedYear, CalendarPage calendarPage) {
        this.currentUser = user;
        this.userManager = userManager;
        this.store = store;
        this.selectedDay = selectedDay;
        this.date = LocalDate.of(selectedYear, selectedMonth, selectedDay);
        this.calendarPage = calendarPage;

        setTitle("Time Slots");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(320, 480);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel datePanel = new JPanel();
        datePanel.add(new JLabel("Date: " + date));

        slotsPanel = new JPanel(new GridLayout(0, 1, 5, 5)); // Vertical layout
        fillSlots();

        add(datePanel, BorderLayout.NORTH);
        add(new JScrollPane(slotsPanel), BorderLayout.CENTER);
        add(createReturnButton(), BorderLayout.SOUTH);
    }

    private void fillSlots() {
        slotsPanel.removeAll();
        for (String time : AppointmentStore.SLOTS) {
            slotsPanel.add(new TimeSlotPanel(time));
        }
        slotsPanel.revalidate();
        slotsPanel.repaint();
    }

    private JButton createReturnButton() {
        JButton returnButton = new JButton("Return");
        returnButton.addActionListener(e -> {
            dispose();
            if (calendarPage != null) {
                calendarPage.refresh();
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
            timeButton.setPreferredSize(new Dimension(230, 30));
            timeButton.setBackground(Color.WHITE);
            timeButton.addActionListener(e -> openScheduleManaging(time));
            add(timeButton);
            updateSlotAvailability(time);
        }

        // What the button shows and whether it can be clicked depends on the user's permissions
        private void updateSlotAvailability(String time) {
            Optional<Appointment> booked = store.find(date, time);
            boolean past = AppointmentStore.isPast(date, time);
            if (booked.isPresent()) {
                Appointment a = booked.get();
                timeButton.setForeground(Color.GRAY);
                if (store.canSee(currentUser, a)) {
                    // Dentist and secretary see who it is; a patient sees only their own
                    timeButton.setText(time + "  -  " + a.getName()
                            + (a.getStatus() == Appointment.Status.CONFIRMED ? "  (confirmed)" : "  (pending)"));
                    timeButton.setEnabled(!past && store.canCancel(currentUser, a));
                } else {
                    timeButton.setText(time + "  -  Booked");
                    timeButton.setEnabled(false);
                }
            } else if (past) {
                timeButton.setEnabled(false);
            } else if (!canBookAnything()) {
                timeButton.setText(time + "  -  Free");
                timeButton.setEnabled(false);
            }
        }

        private void openScheduleManaging(String time) {
            ScheduleManaging scheduleManaging = new ScheduleManaging(currentUser, userManager, store, date, time, () -> {
                fillSlots();
                if (calendarPage != null) {
                    calendarPage.refresh();
                }
            });
            scheduleManaging.setVisible(true);
        }
    }

    private boolean canBookAnything() {
        return currentUser.hasPermission(Permission.BOOK_FOR_SELF)
                || currentUser.hasPermission(Permission.BOOK_FOR_PATIENTS);
    }

    public static void main(String[] args) {
        UserManager userManager = new UserManager();
        userManager.ensureUser("patient1@example.com", "patient1", UserType.CLIENT, "Test Patient");
        TimeSlots timeSlots = new TimeSlots(userManager.getUserByEmail("patient1@example.com"), userManager,
                new AppointmentStore(), 1, 1, 2030, null);
        timeSlots.setVisible(true);
    }
}
