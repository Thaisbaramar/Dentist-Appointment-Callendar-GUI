package Printing;
import java.util.Objects;

import Everything.UserType;

// Abstract User class
public abstract class User {
    private final String email;
    private String password;
    private final UserType userType;

    // Constructor
    public User(String email, String password, UserType userType) {
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.password = Objects.requireNonNull(password, "Password cannot be null");
        this.userType = Objects.requireNonNull(userType, "User type cannot be null");
    }

    // Getters
    public String getEmail() {
        return email;
    }

    public UserType getUserType() {
        return userType;
    }
    
    public String getPassword() {
        return password;
    }

    // Method to authenticate a user
    public boolean authenticate(String password) {
        return this.password.equals(password);
    }

    // Abstract methods for access permissions
    public abstract boolean hasAccessToTimeSlots();

    public abstract boolean hasAccessToScheduleAppointments();

    public abstract boolean hasAccessToCancelAppointments();
}

// Concrete subclass Dentist
class Dentist extends User {
    public Dentist(String email, String password) {
        super(email, password, UserType.DENTIST);
    }

    // Implement abstract methods specific to Dentist
    @Override
    public boolean hasAccessToTimeSlots() {
        return true;
    }

    @Override
    public boolean hasAccessToScheduleAppointments() {
        return false;
    }

    @Override
    public boolean hasAccessToCancelAppointments() {
        return false;
    }
}

// Concrete subclass Staff
class Staff extends User {
    public Staff(String email, String password) {
        super(email, password, UserType.STAFF);
    }

    // Implement abstract methods specific to Staff
    @Override
    public boolean hasAccessToTimeSlots() {
        return true;
    }

    @Override
    public boolean hasAccessToScheduleAppointments() {
        return true;
    }

    @Override
    public boolean hasAccessToCancelAppointments() {
        return true;
    }
}

// Concrete subclass Client
class Client extends User {
    public Client(String email, String password) {
        super(email, password, UserType.CLIENT);
    }

    // Implement abstract methods specific to Client
    @Override
    public boolean hasAccessToTimeSlots() {
        return true;
    }

    @Override
    public boolean hasAccessToScheduleAppointments() {
        return true;
    }

    @Override
    public boolean hasAccessToCancelAppointments() {
        return false;
    }
}

