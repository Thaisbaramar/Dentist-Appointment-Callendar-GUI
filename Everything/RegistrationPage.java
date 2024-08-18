package Everything;
import javax.swing.*;

import Printing.UserManager;

import java.awt.*;
import java.awt.event.*;

public class RegistrationPage extends JFrame {
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    // Reference to the user manager
    private UserManager userManager;

    public RegistrationPage(UserManager userManager) {
        this.userManager = userManager;

        setTitle("Registration Page");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame on the screen

        // Create components
        JPanel registrationPanel = new JPanel(new GridLayout(5, 2));

        JLabel nameLabel = new JLabel("Name:");
        registrationPanel.add(nameLabel);
        nameField = new JTextField();
        registrationPanel.add(nameField);

        JLabel emailLabel = new JLabel("Email:");
        registrationPanel.add(emailLabel);
        emailField = new JTextField();
        registrationPanel.add(emailField);

        JLabel passwordLabel = new JLabel("Password:");
        registrationPanel.add(passwordLabel);
        passwordField = new JPasswordField();
        registrationPanel.add(passwordField);

        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        registrationPanel.add(confirmPasswordLabel);
        confirmPasswordField = new JPasswordField();
        registrationPanel.add(confirmPasswordField);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });
        registrationPanel.add(registerButton);

        // Add panel to the frame
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

        // Registration successful
        boolean success = userManager.registerUser(email, String.valueOf(password), UserType.CLIENT);
        if (success) {
            JOptionPane.showMessageDialog(this, "Registration successful");
            dispose(); // Close registration window
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed. User already exists.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Create an instance of UserManager
        UserManager userManager = new UserManager();

        SwingUtilities.invokeLater(() -> new RegistrationPage(userManager).setVisible(true));
    }
}