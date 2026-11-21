package Printing;
import java.util.Objects;

import Everything.Permission;
import Everything.UserType;

// Base User class. What a user may do comes from the permissions of their UserType.
public abstract class User {
    private final String email;
    private final String password;
    private final UserType userType;
    private final String name;

    // Constructor
    public User(String email, String password, UserType userType, String name) {
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.password = Objects.requireNonNull(password, "Password cannot be null");
        this.userType = Objects.requireNonNull(userType, "User type cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
    }

    // Getters
    public String getEmail() {
        return email;
    }

    public UserType getUserType() {
        return userType;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    // Method to authenticate a user
    public boolean authenticate(String password) {
        return this.password.equals(password);
    }

    public boolean hasPermission(Permission permission) {
        return userType.can(permission);
    }

    public boolean isSameUser(User other) {
        return other != null && email.equalsIgnoreCase(other.email);
    }

    // Convenience checks kept from the first version
    public boolean hasAccessToTimeSlots() {
        return true;
    }

    public boolean hasAccessToScheduleAppointments() {
        return hasPermission(Permission.BOOK_FOR_SELF) || hasPermission(Permission.BOOK_FOR_PATIENTS);
    }

    public boolean hasAccessToCancelAppointments() {
        return hasPermission(Permission.CANCEL_OWN) || hasPermission(Permission.CANCEL_ANY);
    }

    @Override
    public String toString() {
        return name + " (" + userType.getLabel() + ")";
    }
}

class Dentist extends User {
    public Dentist(String email, String password, String name) {
        super(email, password, UserType.DENTIST, name);
    }
}

class Secretary extends User {
    public Secretary(String email, String password, String name) {
        super(email, password, UserType.SECRETARY, name);
    }
}

class Staff extends User {
    public Staff(String email, String password, String name) {
        super(email, password, UserType.STAFF, name);
    }
}

class Client extends User {
    public Client(String email, String password, String name) {
        super(email, password, UserType.CLIENT, name);
    }
}
