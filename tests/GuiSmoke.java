import Everything.*;
import Printing.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.time.LocalDate;
import javax.imageio.ImageIO;

public class GuiSmoke {
    static void shot(JFrame f, String name) throws Exception {
        f.setVisible(true);
        f.validate();
        BufferedImage img = new BufferedImage(f.getWidth(), f.getHeight(), BufferedImage.TYPE_INT_RGB);
        f.paint(img.getGraphics());
        ImageIO.write(img, "png", new java.io.File("/tmp/claude-0/shots/" + name + ".png"));
    }
    public static void main(String[] a) throws Exception {
        Path dir = Files.createTempDirectory("gui");
        UserManager um = new UserManager(dir.resolve("u.txt"));
        AppointmentStore st = new AppointmentStore(dir.resolve("a.txt"));
        um.registerUser("doc@x","d",UserType.DENTIST,"Dr. Doc");
        um.registerUser("sec@x","s",UserType.SECRETARY,"Front Desk");
        um.registerUser("ana@x","a",UserType.CLIENT,"Ana");
        um.registerUser("bob@x","b",UserType.CLIENT,"Bob");
        LocalDate d = LocalDate.now().plusDays(2);
        st.book(um.getUserByEmail("sec@x"), um.getUserByEmail("ana@x"), d, "09:00");
        st.book(um.getUserByEmail("sec@x"), um.getUserByEmail("bob@x"), d, "10:00");
        SwingUtilities.invokeAndWait(() -> {
            try {
                for (String r : new String[]{"ana","doc","sec"}) {
                    User u = um.getUserByEmail(r + "@x");
                    shot(new CalendarPage(u, um, st), "cal-" + r);
                    shot(new TimeSlots(u, um, st, d.getDayOfMonth(), d.getMonthValue(), d.getYear(), null), "slots-" + r);
                    shot(new AppointmentListFrame(u, st, new JFrame()), "list-" + r);
                }
                shot(new ScheduleManaging(um.getUserByEmail("sec@x"), um, st, d, "11:00", null), "book-sec");
                shot(new ScheduleManaging(um.getUserByEmail("ana@x"), um, st, d, "10:00", null), "other-ana");
                System.out.println("GUI OK");
            } catch (Exception e) { e.printStackTrace(); System.exit(1); }
        });
        System.exit(0);
    }
}
