package Printing;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import Everything.UserType;

public class UserManager {
    private final Map<String, User> userMap;

    public UserManager() {
        this.userMap = new HashMap<>();
    }

    public boolean registerUser(String email, String password, UserType userType) {
        Objects.requireNonNull(email, "Email cannot be null");
        Objects.requireNonNull(password, "Password cannot be null");
        Objects.requireNonNull(userType, "User type cannot be null");

        if (userMap.containsKey(email)) {
            System.out.println("User with email " + email + " already exists.");
            return false;
        }

        User user = createUser(email, password, userType);
        userMap.put(email, user);

        // Save user information to file
        saveUserInfoToFile(email, password, userType);

        System.out.println("User registered successfully.");
        return true;
    }

    private void saveUserInfoToFile(String email, String password, UserType userType) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("SavedInfo.txt", true))) {
            writer.write(email + ":" + password + ":" + userType);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private User createUser(String email, String password, UserType userType) {
        switch (userType) {
            case DENTIST:
                return new Dentist(email, password);
            case STAFF:
                return new Staff(email, password);
            case CLIENT:
                return new Client(email, password);
            default:
                throw new IllegalArgumentException("Invalid user type: " + userType);
        }
    }

    public List<User> getUsers() {
        return new ArrayList<>(userMap.values());
    }

    public User validateLogin(String email, String password) {
        if (!userMap.containsKey(email)) {
            return null; // User does not exist
        }

        User user = userMap.get(email);

        // Authenticate the user with the provided password
        boolean isAuthenticated = user.authenticate(password);

        if (isAuthenticated) {
            return user;
        } else {
            return null; // Return null if authentication fails
        }
    }

    public User getUserByEmail(String email) {
        return userMap.get(email);
    }

    public void createStaff(String email, String password) {
        registerUser(email, password, UserType.STAFF);
    }
}
