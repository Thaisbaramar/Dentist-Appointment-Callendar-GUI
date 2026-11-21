package Printing;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import Everything.Permission;
import Everything.UserType;

public class UserManager {
    private final Map<String, User> userMap;
    private final Path file;

    public UserManager() {
        this(Paths.get("TxtFiles", "SavedInfo.txt"));
    }

    public UserManager(Path file) {
        this.userMap = new LinkedHashMap<>();
        this.file = file;
        load();
    }

    /** Public sign-up and internal use: creates the account without checking who is asking. */
    public boolean registerUser(String email, String password, UserType userType) {
        return registerUser(email, password, userType, defaultName(email));
    }

    public synchronized boolean registerUser(String email, String password, UserType userType, String name) {
        Objects.requireNonNull(email, "Email cannot be null");
        Objects.requireNonNull(password, "Password cannot be null");
        Objects.requireNonNull(userType, "User type cannot be null");
        Objects.requireNonNull(name, "Name cannot be null");
        checkField(email);
        checkField(password);
        checkField(name);

        if (userMap.containsKey(email)) {
            System.out.println("User with email " + email + " already exists.");
            return false;
        }

        userMap.put(email, createUser(email, password, userType, name));
        saveUserInfoToFile(email, password, userType, name);
        return true;
    }

    /**
     * Creates an account on behalf of someone who is logged in (or on a public sign-up when actor is null).
     * - public sign-up: patients only
     * - REGISTER_PATIENTS: patients only
     * - MANAGE_ACCOUNTS: any type
     */
    public boolean createAccount(User actor, String email, String password, UserType type, String name) {
        boolean allowed;
        if (actor == null) {
            allowed = type == UserType.CLIENT;
        } else if (actor.hasPermission(Permission.MANAGE_ACCOUNTS)) {
            allowed = true;
        } else {
            allowed = actor.hasPermission(Permission.REGISTER_PATIENTS) && type == UserType.CLIENT;
        }
        if (!allowed) {
            throw new SecurityException("This account type cannot create " + type.getLabel() + " accounts.");
        }
        return registerUser(email, password, type, name);
    }

    /** Creates a built-in account only if it does not exist yet (used for the sample accounts). */
    public void ensureUser(String email, String password, UserType userType, String name) {
        if (!userMap.containsKey(email)) {
            registerUser(email, password, userType, name);
        }
    }

    private static void checkField(String value) {
        if (value.indexOf(':') >= 0 || value.indexOf('\n') >= 0 || value.indexOf('\r') >= 0) {
            throw new IllegalArgumentException("Fields cannot contain ':' or line breaks.");
        }
    }

    private static String defaultName(String email) {
        int at = email.indexOf('@');
        return at > 0 ? email.substring(0, at) : email;
    }

    private void saveUserInfoToFile(String email, String password, UserType userType, String name) {
        try {
            if (file.getParent() != null) {
                Files.createDirectories(file.getParent());
            }
            try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                writer.write(email + ":" + password + ":" + userType + ":" + name);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Reads accounts saved by earlier runs. Old lines without a name (email:password:TYPE) still work. */
    private void load() {
        if (!Files.exists(file)) {
            return;
        }
        try {
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split(":", 4);
                if (parts.length < 3) {
                    continue;
                }
                try {
                    UserType type = UserType.valueOf(parts[2].trim());
                    String name = parts.length == 4 ? parts[3] : defaultName(parts[0]);
                    userMap.putIfAbsent(parts[0], createUser(parts[0], parts[1], type, name));
                } catch (IllegalArgumentException ignored) {
                    // unknown account type in the file: skip the line
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private User createUser(String email, String password, UserType userType, String name) {
        switch (userType) {
            case DENTIST:
                return new Dentist(email, password, name);
            case SECRETARY:
                return new Secretary(email, password, name);
            case STAFF:
                return new Staff(email, password, name);
            case CLIENT:
                return new Client(email, password, name);
            default:
                throw new IllegalArgumentException("Invalid user type: " + userType);
        }
    }

    public synchronized List<User> getUsers() {
        return new ArrayList<>(userMap.values());
    }

    /** Registered patients, sorted by name. Used when the secretary books for someone. */
    public synchronized List<User> getPatients() {
        List<User> patients = new ArrayList<>();
        for (User u : userMap.values()) {
            if (u.getUserType() == UserType.CLIENT) {
                patients.add(u);
            }
        }
        patients.sort(Comparator.comparing(u -> u.getName().toLowerCase()));
        return patients;
    }

    public synchronized User validateLogin(String email, String password) {
        User user = userMap.get(email);
        if (user == null) {
            return null; // User does not exist
        }
        return user.authenticate(password) ? user : null;
    }

    public synchronized User getUserByEmail(String email) {
        return userMap.get(email);
    }

    public void createStaff(String email, String password) {
        registerUser(email, password, UserType.STAFF);
    }

    public void createSecretary(String email, String password, String name) {
        registerUser(email, password, UserType.SECRETARY, name);
    }
}
