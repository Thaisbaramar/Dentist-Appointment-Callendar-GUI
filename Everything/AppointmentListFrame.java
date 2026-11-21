package Everything;

import javax.swing.*;

import Printing.Appointment;
import Printing.User;

import java.awt.*;
import java.util.List;

/** Lists the appointments the logged-in user is allowed to see (all for dentist/secretary/staff, own for patients). */
public class AppointmentListFrame extends JFrame {
    private final User currentUser;
    private final AppointmentStore store;
    private final JFrame previousPage;
    private DefaultListModel<Appointment> model;
    private JList<Appointment> list;

    public AppointmentListFrame(User currentUser, AppointmentStore store, JFrame previousPage) {
        this.currentUser = currentUser;
        this.store = store;
        this.previousPage = previousPage;
        initComponents();
    }

    private void initComponents() {
        boolean all = currentUser.hasPermission(Permission.VIEW_ALL_APPOINTMENTS);
        setTitle(all ? "Scheduled Appointments (all patients)" : "My Appointments");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(620, 320);
        setLocationRelativeTo(null);

        model = new DefaultListModel<>();
        list = new JList<>(model);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object value, int index, boolean sel, boolean focus) {
                Appointment a = (Appointment) value;
                String text = a.getDate() + "  " + a.getTimeSlot() + "  -  " + a.getName()
                        + (all ? "  <" + a.getPatientEmail() + ">" : "")
                        + "  [" + a.getStatus().toString().toLowerCase() + "]";
                return super.getListCellRendererComponent(l, text, index, sel, focus);
            }
        });
        reload();

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout());
        JButton cancelButton = new JButton("Cancel Selected");
        cancelButton.addActionListener(e -> cancelSelected());
        // Only the dentist (and staff) can confirm; the patient is notified
        if (currentUser.hasPermission(Permission.RESPOND_TO_APPOINTMENTS)) {
            JButton confirmButton = new JButton("Confirm Selected");
            confirmButton.addActionListener(e -> confirmSelected());
            buttons.add(confirmButton);
        }
        if (currentUser.hasPermission(Permission.CANCEL_ANY) || currentUser.hasPermission(Permission.CANCEL_OWN)
                || currentUser.hasPermission(Permission.RESPOND_TO_APPOINTMENTS)) {
            buttons.add(cancelButton);
        }
        JButton returnButton = new JButton("Return to Calendar");
        returnButton.addActionListener(e -> {
            dispose();
            if (previousPage instanceof CalendarPage) {
                ((CalendarPage) previousPage).refresh();
            }
            previousPage.setVisible(true);
        });
        buttons.add(returnButton);
        contentPane.add(buttons, BorderLayout.SOUTH);

        setContentPane(contentPane);
    }

    private void reload() {
        model.clear();
        List<Appointment> visible = store.visibleTo(currentUser);
        for (Appointment a : visible) {
            model.addElement(a);
        }
    }

    private void confirmSelected() {
        Appointment a = list.getSelectedValue();
        if (a == null) {
            JOptionPane.showMessageDialog(this, "Select an appointment first.");
            return;
        }
        try {
            store.confirm(currentUser, a.getDate(), a.getTimeSlot());
            JOptionPane.showMessageDialog(this, "Appointment confirmed. " + a.getName() + " was notified.");
            reload();
        } catch (SecurityException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot confirm", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelSelected() {
        Appointment a = list.getSelectedValue();
        if (a == null) {
            JOptionPane.showMessageDialog(this, "Select an appointment first.");
            return;
        }
        String reason = null;
        if (!a.belongsTo(currentUser)) {
            reason = JOptionPane.showInputDialog(this, "Reason for canceling (the patient will be notified):");
            if (reason == null) {
                return;
            }
        }
        try {
            store.cancel(currentUser, a.getDate(), a.getTimeSlot(), reason);
            reload();
        } catch (SecurityException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot cancel", JOptionPane.ERROR_MESSAGE);
        }
    }
}
