 package Everything;
import javax.swing.*;

import Printing.User;
import Printing.UserManager;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class LoginPage extends JFrame {

    // GUI components
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton goToRegistrationButton;

    // Data storage
    private UserManager userManager;

    public LoginPage(UserManager userManager) {
        // Initialize JFrame
        setTitle("Login Page");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize UserManager
        this.userManager = userManager;

        // Initialize GUI components
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(3, 2));

        JLabel emailLabel = new JLabel("Email:");
        loginPanel.add(emailLabel);
        emailField = new JTextField();
        loginPanel.add(emailField);

        JLabel passwordLabel = new JLabel("Password:");
        loginPanel.add(passwordLabel);
        passwordField = new JPasswordField();
        loginPanel.add(passwordField);

        loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        loginPanel.add(loginButton);

        goToRegistrationButton = new JButton("Go to Registration");
        goToRegistrationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openRegistration();
            }
        });
        loginPanel.add(goToRegistrationButton);

        add(loginPanel);
        setVisible(true);
    }

    // Make waitForLogin() method public
    public UserType waitForLogin() {
        // TODO: Implement login logic here
        return null; // Placeholder return statement
    }

    private void login() {
        String enteredEmail = emailField.getText();
        String enteredPassword = new String(passwordField.getPassword());

        // Retrieve the user from UserManager
        User user = userManager.getUserByEmail(enteredEmail);

        if (user != null) {
            // User found
            if (user.getPassword().equals(enteredPassword)) {
                // Correct password
                JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Return the user type
                UserType userType = user.getUserType();
                // Perform actions based on user type (e.g., open specific page)
                openHomePage(userType);
            } else {
                // Incorrect password
                JOptionPane.showMessageDialog(this, "Login failed. Incorrect password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // User not found
            JOptionPane.showMessageDialog(this, "Login failed. User not found.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openHomePage(UserType userType) {
        // Open the CalendarPage for all types of users
        CalendarPage calendarPage = new CalendarPage(userType);
        calendarPage.setVisible(true);
        // Close the login page
        dispose();
    }


    private void openRegistration() {
        RegistrationPage registrationPage = new RegistrationPage(userManager); // Pass userManager to RegistrationPage constructor
        registrationPage.setVisible(true);
    }

    public static void main(String[] args) {
        UserManager userManager = new UserManager(); // Initialize UserManager
        userManager.createStaff("staff_username", "staff_password"); // Create staff user
        userManager.registerUser("dentist@example.com", "dentist_password", UserType.DENTIST); // Register dentist user
        userManager.registerUser("client@example.com", "client_password", UserType.CLIENT); // Register client user

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginPage(userManager);
            }
        });
    }
}