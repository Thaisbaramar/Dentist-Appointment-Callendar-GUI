import Everything.*;
import Printing.*;

import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;

/** Plain-Java checks of the access rules (no JUnit needed): javac, then java AccessControlTest. */
public class AccessControlTest {
    static int failures = 0;

    static void check(String name, boolean ok) {
        System.out.println((ok ? "PASS  " : "FAIL  ") + name);
        if (!ok) failures++;
    }

    static boolean denied(Runnable r) {
        try { r.run(); return false; } catch (SecurityException e) { return true; }
    }

    public static void main(String[] args) throws Exception {
        Path dir = Files.createTempDirectory("dentist-test");
        UserManager um = new UserManager(dir.resolve("users.txt"));
        AppointmentStore store = new AppointmentStore(dir.resolve("appts.txt"));
        um.registerUser("staff@x", "s", UserType.STAFF, "Staff");
        um.registerUser("doc@x", "d", UserType.DENTIST, "Doc");
        um.registerUser("sec@x", "e", UserType.SECRETARY, "Sec");
        um.registerUser("ana@x", "a", UserType.CLIENT, "Ana");
        um.registerUser("bob@x", "b", UserType.CLIENT, "Bob");
        User staff = um.getUserByEmail("staff@x"), doc = um.getUserByEmail("doc@x"), sec = um.getUserByEmail("sec@x");
        User ana = um.getUserByEmail("ana@x"), bob = um.getUserByEmail("bob@x");
        LocalDate d = LocalDate.now().plusDays(3);

        // Patient
        check("patient books for self", store.book(ana, ana, d, "09:00") != null);
        check("patient cannot book for another patient", denied(() -> store.book(ana, bob, d, "10:00")));
        store.book(sec, bob, d, "10:00");
        check("patient sees only own appointments", store.visibleTo(ana).size() == 1 && store.visibleTo(bob).size() == 1);
        check("patient cannot cancel another's appointment", denied(() -> store.cancel(ana, d, "10:00")));

        // Dentist
        check("dentist sees all appointments", store.visibleTo(doc).size() == 2);
        check("dentist cannot book", denied(() -> store.book(doc, ana, d, "11:00")));
        check("dentist cannot book for self or patients", boolThrows(() -> store.book(doc, doc, d, "13:00")));
        check("patient cannot confirm", denied(() -> store.confirm(ana, d, "09:00")));
        check("secretary cannot confirm", denied(() -> store.confirm(sec, d, "09:00")));
        check("new appointment is pending", store.find(d, "09:00").get().getStatus() == Appointment.Status.PENDING);
        check("dentist confirms and patient is notified", store.confirm(doc, d, "09:00").getStatus() == Appointment.Status.CONFIRMED
                && store.unreadFor(ana).size() == 1 && store.unreadFor(bob).stream().noneMatch(n -> n.getMessage().contains("confirmed")));
        check("confirming twice is refused", boolThrows(() -> store.confirm(doc, d, "09:00")));
        check("patient cannot read another patient's notifications", store.notificationsFor(bob).stream().noneMatch(n -> n.getMessage().contains("confirmed")));
        store.cancel(doc, d, "10:00", "Dentist is out sick");
        check("dentist cancels with a reason and the patient is notified", store.find(d, "10:00").isEmpty()
                && store.unreadFor(bob).stream().anyMatch(n -> n.getMessage().contains("Dentist is out sick")));
        store.book(sec, bob, d, "10:00");
        store.book(bob, bob, d, "14:00");
        int noticesBefore = store.notificationsFor(bob).size();
        store.cancel(bob, d, "14:00");
        check("patient cancelling own appointment sends no notice", store.notificationsFor(bob).size() == noticesBefore);

        // Secretary
        check("secretary sees all appointments", store.visibleTo(sec).size() == 2);
        check("secretary books for a patient", store.book(sec, ana, d, "11:00") != null);
        check("secretary cannot book for herself", boolThrows(() -> store.book(sec, sec, d, "12:00")));
        check("secretary cancels any appointment", store.cancel(sec, d, "11:00") != null);

        // Staff
        check("staff sees all and cancels any", store.visibleTo(staff).size() == 2 && store.cancel(staff, d, "10:00") != null);

        // Slot rules
        check("double booking refused", store.find(d, "09:00").isPresent() && boolThrows(() -> store.book(bob, bob, d, "09:00")));
        check("past slot refused", boolThrows(() -> store.book(bob, bob, LocalDate.now().minusDays(1), "09:00")));

        // Accounts
        check("public sign-up creates only patients", denied(() -> um.createAccount(null, "x@x", "p", UserType.STAFF, "X"))
                && um.createAccount(null, "new@x", "p", UserType.CLIENT, "New"));
        check("secretary registers patients only", um.createAccount(sec, "p2@x", "p", UserType.CLIENT, "P2")
                && denied(() -> um.createAccount(sec, "s2@x", "p", UserType.SECRETARY, "S2")));
        check("dentist and patient cannot create accounts", denied(() -> um.createAccount(doc, "q@x", "p", UserType.CLIENT, "Q"))
                && denied(() -> um.createAccount(ana, "q@x", "p", UserType.CLIENT, "Q")));
        check("staff creates a secretary account", um.createAccount(staff, "sec2@x", "p", UserType.SECRETARY, "Sec2"));

        // Persistence
        AppointmentStore reloaded = new AppointmentStore(dir.resolve("appts.txt"));
        UserManager usersReloaded = new UserManager(dir.resolve("users.txt"));
        check("appointments persist", reloaded.visibleTo(staff).size() == 1);
        check("accounts and roles persist", usersReloaded.getUserByEmail("sec2@x").getUserType() == UserType.SECRETARY);

        System.out.println(failures == 0 ? "\nAll checks passed." : "\n" + failures + " check(s) FAILED.");
        System.exit(failures == 0 ? 0 : 1);
    }

    static boolean boolThrows(Runnable r) {
        try { r.run(); return false; } catch (RuntimeException e) { return true; }
    }
}
