package com.fot.system.view.login;

import com.fot.system.controller.LoginController;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginView() {

        setTitle("TEC-SIS Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("TEC-SIS Login", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(title, gbc);

        // Email Label
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);

        // Email Field
        gbc.gridx = 1;
        emailField = new JTextField();
        panel.add(emailField, gbc);

        // Password Label
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);

        // Password Field
        gbc.gridx = 1;
        passwordField = new JPasswordField();
        panel.add(passwordField, gbc);

        // Login Button
        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(0, 123, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        add(panel);

        // Attach Controller
        new LoginController(this);
    }

    // 🔹 Getters (Controller will use these)

    public String getEmail() {
        return emailField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public JButton getLoginButton() {
        return loginButton;
    }
}