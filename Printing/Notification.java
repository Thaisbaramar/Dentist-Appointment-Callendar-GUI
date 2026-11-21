package Printing;

import java.time.LocalDateTime;

/** A message for one patient, e.g. "Your appointment was confirmed". */
public class Notification {
    private final LocalDateTime time;
    private final String recipientEmail;
    private final String message;
    private boolean read;

    public Notification(LocalDateTime time, String recipientEmail, String message, boolean read) {
        this.time = time;
        this.recipientEmail = recipientEmail;
        this.message = message;
        this.read = read;
    }

    public LocalDateTime getTime() { return time; }
    public String getRecipientEmail() { return recipientEmail; }
    public String getMessage() { return message; }
    public boolean isRead() { return read; }
    public void markRead() { read = true; }
}
