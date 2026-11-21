package Everything;

import Printing.Appointment;
import Printing.Notification;
import Printing.User;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * All appointments, saved to a text file (one per line: date|time|patientEmail|patientName).
 * Every method that changes or lists data takes the logged-in user and checks their permissions,
 * so the rules hold no matter which screen calls it.
 */
public class AppointmentStore {
    public static final List<String> SLOTS = new ArrayList<>();
    static {
        for (int hour = 8; hour <= 17; hour++) {
            SLOTS.add(String.format("%02d:00", hour));
        }
    }

    private final Path file;
    private final Path notificationsFile;
    private final List<Appointment> items = new ArrayList<>();
    private final List<Notification> notifications = new ArrayList<>();

    public AppointmentStore() {
        this(Paths.get("TxtFiles", "appointments.txt"));
    }

    public AppointmentStore(Path file) {
        this.file = file;
        this.notificationsFile = file.resolveSibling("notifications.txt");
        load();
        loadNotifications();
    }

    // ---------- reading ----------

    /** Appointments the user is allowed to see, sorted by date and time. */
    public synchronized List<Appointment> visibleTo(User actor) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment a : items) {
            if (canSee(actor, a)) {
                result.add(a);
            }
        }
        result.sort(Comparator.comparing(Appointment::getDate).thenComparing(Appointment::getTimeSlot));
        return result;
    }

    public boolean canSee(User actor, Appointment a) {
        return actor.hasPermission(Permission.VIEW_ALL_APPOINTMENTS)
                || (actor.hasPermission(Permission.VIEW_OWN_APPOINTMENTS) && a.belongsTo(actor));
    }

    /** The appointment in a slot, whoever owns it. Screens decide how much of it to show. */
    public synchronized Optional<Appointment> find(LocalDate date, String time) {
        for (Appointment a : items) {
            if (a.getDate().equals(date) && a.getTimeSlot().equals(time)) {
                return Optional.of(a);
            }
        }
        return Optional.empty();
    }

    public synchronized boolean isFull(LocalDate date) {
        int count = 0;
        for (Appointment a : items) {
            if (a.getDate().equals(date)) {
                count++;
            }
        }
        return count >= SLOTS.size();
    }

    public synchronized boolean hasAppointmentsVisibleTo(User actor, LocalDate date) {
        for (Appointment a : items) {
            if (a.getDate().equals(date) && canSee(actor, a)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPast(LocalDate date, String time) {
        LocalDateTime slot = LocalDateTime.of(date, LocalTime.parse(time));
        return !slot.isAfter(LocalDateTime.now());
    }

    // ---------- changing ----------

    /**
     * Books a slot. A patient books for themself (BOOK_FOR_SELF).
     * Someone with BOOK_FOR_PATIENTS can book for any registered patient.
     *
     * @throws SecurityException     the actor is not allowed to book this
     * @throws IllegalStateException the slot is taken or already in the past
     */
    public synchronized Appointment book(User actor, User patient, LocalDate date, String time) {
        if (patient == null || patient.getUserType() != UserType.CLIENT) {
            throw new IllegalArgumentException("Appointments are booked for a registered patient.");
        }
        boolean forSelf = actor.isSameUser(patient);
        if (forSelf ? !actor.hasPermission(Permission.BOOK_FOR_SELF)
                    : !actor.hasPermission(Permission.BOOK_FOR_PATIENTS)) {
            throw new SecurityException("Your account type is not allowed to book this appointment.");
        }
        if (!SLOTS.contains(time)) {
            throw new IllegalArgumentException("Unknown time slot: " + time);
        }
        if (isPast(date, time)) {
            throw new IllegalStateException("That time has already passed.");
        }
        if (find(date, time).isPresent()) {
            throw new IllegalStateException("That time slot is already booked.");
        }
        Appointment appointment = new Appointment(date, time, patient.getEmail(), patient.getName());
        items.add(appointment);
        if (!actor.isSameUser(patient)) {
            notify(patient, "An appointment was booked for you on " + date + " at " + time
                    + " by " + actor.getName() + ". It is waiting for the dentist's confirmation.");
        }
        save();
        return appointment;
    }

    /**
     * Confirms an appointment. Only the dentist (RESPOND_TO_APPOINTMENTS) can do this.
     * The patient is notified.
     */
    public synchronized Appointment confirm(User actor, LocalDate date, String time) {
        if (!actor.hasPermission(Permission.RESPOND_TO_APPOINTMENTS)) {
            throw new SecurityException("Your account type is not allowed to confirm appointments.");
        }
        Appointment appointment = find(date, time)
                .orElseThrow(() -> new IllegalStateException("No appointment scheduled for " + time));
        if (appointment.getStatus() == Appointment.Status.CONFIRMED) {
            throw new IllegalStateException("This appointment is already confirmed.");
        }
        if (isPast(date, time)) {
            throw new IllegalStateException("That time has already passed.");
        }
        appointment.setStatus(Appointment.Status.CONFIRMED);
        notifyPatientOf(appointment, "Your appointment on " + date + " at " + time
                + " was confirmed by " + actor.getName() + ".");
        save();
        return appointment;
    }

    public Appointment cancel(User actor, LocalDate date, String time) {
        return cancel(actor, date, time, null);
    }

    /**
     * Cancels the appointment in a slot. A patient can cancel only their own (CANCEL_OWN).
     * Someone with CANCEL_ANY or RESPOND_TO_APPOINTMENTS can cancel any appointment.
     * When someone other than the patient cancels, the patient is notified (with the reason, if given).
     *
     * @throws SecurityException     the actor is not allowed to cancel it
     * @throws IllegalStateException there is nothing booked in that slot
     */
    public synchronized Appointment cancel(User actor, LocalDate date, String time, String reason) {
        Appointment appointment = find(date, time)
                .orElseThrow(() -> new IllegalStateException("No appointment scheduled for " + time));
        if (!canCancel(actor, appointment)) {
            throw new SecurityException("Your account type is not allowed to cancel this appointment.");
        }
        items.remove(appointment);
        if (!appointment.belongsTo(actor)) {
            String why = (reason == null || reason.isBlank()) ? "" : " Reason: " + reason.trim();
            notifyPatientOf(appointment, "Your appointment on " + date + " at " + time
                    + " was canceled by " + actor.getName() + "." + why);
        }
        save();
        return appointment;
    }

    public boolean canCancel(User actor, Appointment appointment) {
        return actor.hasPermission(Permission.CANCEL_ANY)
                || actor.hasPermission(Permission.RESPOND_TO_APPOINTMENTS)
                || (actor.hasPermission(Permission.CANCEL_OWN) && appointment.belongsTo(actor));
    }

    public boolean canConfirm(User actor, Appointment appointment) {
        return actor.hasPermission(Permission.RESPOND_TO_APPOINTMENTS)
                && appointment.getStatus() == Appointment.Status.PENDING
                && !isPast(appointment.getDate(), appointment.getTimeSlot());
    }

    // ---------- notifications ----------

    /** A user's own notifications, newest first. Nobody can read another user's. */
    public synchronized List<Notification> notificationsFor(User user) {
        List<Notification> result = new ArrayList<>();
        for (Notification n : notifications) {
            if (n.getRecipientEmail().equalsIgnoreCase(user.getEmail())) {
                result.add(n);
            }
        }
        result.sort(Comparator.comparing(Notification::getTime).reversed());
        return result;
    }

    public synchronized List<Notification> unreadFor(User user) {
        List<Notification> result = new ArrayList<>();
        for (Notification n : notificationsFor(user)) {
            if (!n.isRead()) {
                result.add(n);
            }
        }
        return result;
    }

    public synchronized void markAllRead(User user) {
        for (Notification n : notificationsFor(user)) {
            n.markRead();
        }
        saveNotifications();
    }

    private void notifyPatientOf(Appointment appointment, String message) {
        notifications.add(new Notification(LocalDateTime.now(), appointment.getPatientEmail(), message, false));
        saveNotifications();
    }

    private void notify(User patient, String message) {
        notifications.add(new Notification(LocalDateTime.now(), patient.getEmail(), message, false));
        saveNotifications();
    }

    // ---------- file ----------

    private void load() {
        if (!Files.exists(file)) {
            return;
        }
        try {
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }
                String[] p = line.split("\\|", 5);
                if (p.length < 4) {
                    continue;
                }
                try {
                    Appointment a = new Appointment(LocalDate.parse(p[0]), p[1], p[2], p[3]);
                    if (p.length == 5) {
                        a.setStatus(Appointment.Status.valueOf(p[4].trim()));
                    }
                    items.add(a);
                } catch (RuntimeException ignored) {
                    // malformed line: skip it
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading appointments: " + e.getMessage());
        }
    }

    private void save() {
        List<String> lines = new ArrayList<>();
        lines.add("# date|time|patient email|patient name|status");
        for (Appointment a : items) {
            lines.add(a.getDate() + "|" + a.getTimeSlot() + "|" + clean(a.getPatientEmail()) + "|" + clean(a.getName()) + "|" + a.getStatus());
        }
        try {
            if (file.getParent() != null) {
                Files.createDirectories(file.getParent());
            }
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Error saving appointments: " + e.getMessage());
        }
    }

    private void loadNotifications() {
        if (!Files.exists(notificationsFile)) {
            return;
        }
        try {
            for (String line : Files.readAllLines(notificationsFile, StandardCharsets.UTF_8)) {
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }
                String[] p = line.split("\\|", 4);
                if (p.length < 4) {
                    continue;
                }
                try {
                    notifications.add(new Notification(LocalDateTime.parse(p[0]), p[1], p[3], Boolean.parseBoolean(p[2])));
                } catch (RuntimeException ignored) {
                    // malformed line: skip it
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading notifications: " + e.getMessage());
        }
    }

    private void saveNotifications() {
        List<String> lines = new ArrayList<>();
        lines.add("# time|patient email|read|message");
        for (Notification n : notifications) {
            lines.add(n.getTime() + "|" + clean(n.getRecipientEmail()) + "|" + n.isRead() + "|" + clean(n.getMessage()));
        }
        try {
            if (notificationsFile.getParent() != null) {
                Files.createDirectories(notificationsFile.getParent());
            }
            Files.write(notificationsFile, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Error saving notifications: " + e.getMessage());
        }
    }

    private static String clean(String value) {
        return value.replace('|', ' ').replace('\n', ' ').replace('\r', ' ');
    }
}
