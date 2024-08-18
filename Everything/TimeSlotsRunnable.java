package Everything;
import java.util.concurrent.TimeUnit;

public class TimeSlotsRunnable implements Runnable {
    private UserType userType;
    private int selectedDay;
    private int selectedMonth;
    private int selectedYear;

    public TimeSlotsRunnable(UserType userType, int selectedDay, int selectedMonth, int selectedYear) {
        this.userType = userType;
        this.selectedDay = selectedDay;
        this.selectedMonth = selectedMonth;
        this.selectedYear = selectedYear;
    }

    @Override
    public void run() {
    	// Simulate loading time slots data
        System.out.println("Loading time slots data for " + userType + "...");
        try {
            TimeUnit.SECONDS.sleep(2); // Simulating loading time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Reset the interrupted status
            System.err.println("Loading time slots interrupted: " + e.getMessage());
            return;
        }
        System.out.println("Time slots data loaded for " + userType);
        // Implement your logic to load and display time slots here
    }

}
