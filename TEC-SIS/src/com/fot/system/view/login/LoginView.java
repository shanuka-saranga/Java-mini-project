package com.fot.system.view.login;

import com.fot.system.config.AppTheme;
import com.fot.system.controller.LoginController;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginView extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginView(String email, String password) {

        setTitle("TEC-SIS | Login");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        JPanel headerBar = new JPanel();
        headerBar.setBackground(AppTheme.PRIMARY);
        headerBar.setPreferredSize(new Dimension(450, 10));
        mainPanel.add(headerBar, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 10, 5);

        JLabel titleLabel = new JLabel("Welcome Back", JLabel.CENTER);
        titleLabel.setFont(AppTheme.LOGIN_TITLE_FONT);
        titleLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 30, 0);
        formPanel.add(titleLabel, gbc);

        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel emailLabel = createFieldLabel("Email Address");
        gbc.gridy = 1;
        formPanel.add(emailLabel, gbc);

        emailField = createTextInput();
        emailField.setText(email == null ? "" : email);
        gbc.gridy = 2;
        formPanel.add(emailField, gbc);

        JLabel passLabel = createFieldLabel("Password");
        gbc.gridy = 3;
        gbc.insets = new Insets(15, 5, 5, 5);
        formPanel.add(passLabel, gbc);

        passwordField = createPasswordInput();
        passwordField.setText(password == null ? "" : password);
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 5, 5, 5);
        formPanel.add(passwordField, gbc);

        loginButton = new JButton("Login Now");
        loginButton.setFont(AppTheme.LOGIN_BUTTON_FONT);
        loginButton.setBackground(AppTheme.PRIMARY);
        loginButton.setForeground(Color.WHITE);
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setPreferredSize(new Dimension(200, 45));

        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(loginButton, gbc);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);

        SwingUtilities.invokeLater(() -> {
            if (emailField.getText().trim().isEmpty()) {
                emailField.requestFocusInWindow();
            } else {
                passwordField.requestFocusInWindow();
            }
        });

        new LoginController(this, email, password);
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.FORM_LABEL_FONT);
        return label;
    }

    private JTextField createTextInput() {
        JTextField textField = new JTextField(20);
        styleInputField(textField);
        return textField;
    }

    private JPasswordField createPasswordInput() {
        JPasswordField passField = new JPasswordField(20);
        styleInputField(passField);
        return passField;
    }

    private void styleInputField(JTextField field) {
        field.setFont(AppTheme.FORM_INPUT_FONT);
        field.setPreferredSize(new Dimension(0, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    public String getEmail() {
        return emailField.getText().trim();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public JButton getLoginButton() {
        return loginButton;
    }
}
