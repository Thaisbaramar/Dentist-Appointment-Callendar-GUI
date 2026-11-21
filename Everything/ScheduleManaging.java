package Everything;

import javax.swing.*;

import Printing.Appointment;
import Printing.User;
import Printing.UserManager;

import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Book or cancel one slot. What is offered follows the logged-in user's permissions,
 * and AppointmentStore checks them again when the button is pressed.
 */
public class ScheduleManaging extends JFrame {
    private final User currentUser;
    private final AppointmentStore store;
    private final LocalDate date;
    private final String timeSlot;
    private final Runnable onChange;
    private JComboBox<User> patientBox; // only when the user may book for patients

    public ScheduleManaging(User currentUser, UserManager userManager, AppointmentStore store,
                            LocalDate date, String timeSlot, Runnable onChange) {
        this.currentUser = currentUser;
        this.store = store;
        this.date = date;
        this.timeSlot = timeSlot;
        this.onChange = onChange;
        initComponents(userManager);
    }

    private void initComponents(UserManager userManager) {
        setTitle("Schedule Managing");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(480, 200);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(new JLabel("Selected: " + date + " at " + timeSlot), BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridLayout(2, 1));
        JPanel buttonPanel = new JPanel(new FlowLayout());

        Optional<Appointment> booked = store.find(date, timeSlot);
        if (booked.isPresent()) {
            Appointment a = booked.get();
            inputPanel.add(new JLabel("Booked for:"));
            inputPanel.add(new JLabel(store.canSee(currentUser, a)
                    ? a.getName() + " (" + a.getStatus().toString().toLowerCase() + ")" : "Another patient"));
            if (store.canConfirm(currentUser, a)) {
                JButton confirmButton = new JButton("Confirm Appointment");
                confirmButton.addActionListener(e -> confirmAppointment());
                buttonPanel.add(confirmButton);
            }
            if (store.canCancel(currentUser, a)) {
                JButton cancelButton = new JButton("Cancel Appointment");
                cancelButton.addActionListener(e -> cancelAppointment(a));
                buttonPanel.add(cancelButton);
            }
        } else if (currentUser.hasPermission(Permission.BOOK_FOR_PATIENTS)) {
            List<User> patients = userManager.getPatients();
            inputPanel.add(new JLabel("Patient:"));
            patientBox = new JComboBox<>(patients.toArray(new User[0]));
            patientBox.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean sel, boolean focus) {
                    User u = (User) value;
                    return super.getListCellRendererComponent(list, u == null ? "" : u.getName() + " <" + u.getEmail() + ">", index, sel, focus);
                }
            });
            inputPanel.add(patientBox);
            JButton scheduleButton = new JButton("Schedule Appointment");
            scheduleButton.setEnabled(!patients.isEmpty());
            scheduleButton.addActionListener(e -> scheduleAppointment((User) patientBox.getSelectedItem()));
            buttonPanel.add(scheduleButton);
        } else if (currentUser.hasPermission(Permission.BOOK_FOR_SELF)) {
            inputPanel.add(new JLabel("Patient:"));
            inputPanel.add(new JLabel(currentUser.getName()));
            JButton scheduleButton = new JButton("Schedule Appointment");
            scheduleButton.addActionListener(e -> scheduleAppointment(currentUser));
            buttonPanel.add(scheduleButton);
        } else {
            inputPanel.add(new JLabel("Your account type cannot book appointments."));
            inputPanel.add(new JLabel(""));
        }

        JButton returnButton = new JButton("Return");
        returnButton.addActionListener(e -> dispose());
        buttonPanel.add(returnButton);

        contentPane.add(inputPanel, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        setContentPane(contentPane);
    }

    private void scheduleAppointment(User patient) {
        try {
            store.book(currentUser, patient, date, timeSlot);
            JOptionPane.showMessageDialog(this, "Appointment scheduled for " + timeSlot + " with " + patient.getName());
            finish();
        } catch (SecurityException | IllegalStateException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot schedule", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void confirmAppointment() {
        try {
            Appointment confirmed = store.confirm(currentUser, date, timeSlot);
            JOptionPane.showMessageDialog(this, "Appointment confirmed. " + confirmed.getName() + " was notified.");
            finish();
        } catch (SecurityException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot confirm", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelAppointment(Appointment a) {
        String reason = null;
        if (!a.belongsTo(currentUser)) {
            // Someone else's appointment: the patient will be told, so ask why
            reason = JOptionPane.showInputDialog(this, "Reason for canceling (the patient will be notified):");
            if (reason == null) {
                return; // pressed Cancel in the dialog
            }
        }
        try {
            Appointment cancelled = store.cancel(currentUser, date, timeSlot, reason);
            JOptionPane.showMessageDialog(this, "Appointment canceled for " + timeSlot + " with " + cancelled.getName()
                    + (a.belongsTo(currentUser) ? "" : ". The patient was notified."));
            finish();
        } catch (SecurityException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot cancel", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void finish() {
        dispose();
        if (onChange != null) {
            onChange.run();
        }
    }
}
