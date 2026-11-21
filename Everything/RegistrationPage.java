package Everything;
import javax.swing.*;

import Printing.User;
import Printing.UserManager;

import java.awt.*;
import java.awt.event.*;

/**
 * Three ways to open it:
 * - from the login page: a visitor creates their own patient account
 * - a secretary (REGISTER_PATIENTS): registers a patient
 * - staff (MANAGE_ACCOUNTS): creates an account of any type
 */
public class RegistrationPage extends JFrame {
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<UserType> typeBox; // only for MANAGE_ACCOUNTS

    private final UserManager userManager;
    private final User actor; // null on public sign-up

    public RegistrationPage(UserManager userManager) {
        this(userManager, null);
    }

    public RegistrationPage(UserManager userManager, User actor) {
        if (actor != null && !actor.hasPermission(Permission.REGISTER_PATIENTS)
                && !actor.hasPermission(Permission.MANAGE_ACCOUNTS)) {
            throw new SecurityException("This account type cannot register users.");
        }
        this.userManager = userManager;
        this.actor = actor;
        boolean chooseType = actor != null && actor.hasPermission(Permission.MANAGE_ACCOUNTS);

        setTitle(actor == null ? "Registration Page" : chooseType ? "Manage Accounts" : "Register Patient");
        setSize(400, chooseType ? 240 : 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame on the screen

        // Create components
        JPanel registrationPanel = new JPanel(new GridLayout(chooseType ? 6 : 5, 2));

        registrationPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        registrationPanel.add(nameField);

        registrationPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        registrationPanel.add(emailField);

        registrationPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        registrationPanel.add(passwordField);

        registrationPanel.add(new JLabel("Confirm Password:"));
        confirmPasswordField = new JPasswordField();
        registrationPanel.add(confirmPasswordField);

        if (chooseType) {
            registrationPanel.add(new JLabel("Account type:"));
            typeBox = new JComboBox<>(UserType.values());
            typeBox.setSelectedItem(UserType.CLIENT);
            registrationPanel.add(typeBox);
        }

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });
        registrationPanel.add(registerButton);

        add(registrationPanel);
    }

    private void register() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        char[] password = passwordField.getPassword();
        char[] confirmPassword = confirmPasswordField.getPassword();

        // Validate inputs
        if (name.isEmpty() || email.isEmpty() || password.length == 0 || confirmPassword.length == 0) {
            JOptionPane.showMessageDialog(this, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!String.valueOf(password).equals(String.valueOf(confirmPassword))) {
            JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserType type = typeBox != null ? (UserType) typeBox.getSelectedItem() : UserType.CLIENT;
        try {
            boolean success = userManager.createAccount(actor, email, String.valueOf(password), type, name);
            if (success) {
                JOptionPane.showMessageDialog(this, "Registration successful");
                dispose(); // Close registration window
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed. User already exists.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException | SecurityException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        UserManager userManager = new UserManager();
        SwingUtilities.invokeLater(() -> new RegistrationPage(userManager).setVisible(true));
    }
}
