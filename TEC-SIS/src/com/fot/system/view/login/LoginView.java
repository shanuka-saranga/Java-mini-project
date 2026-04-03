package com.fot.system.view.login;

import com.fot.system.controller.LoginController;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginView extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;

    // Primary Color: Teal
    private final Color TEAL_PRIMARY = new Color(0, 128, 128);
    private final Color TEAL_HOVER = new Color(0, 102, 102);

    public LoginView() {
        setTitle("TEC-SIS | Login");
        setSize(450, 400); // පොඩ්ඩක් පළල සහ උස වැඩි කළා පිළිවෙළට පේන්න
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false); // Window එක resize කරන්න බැරි කළා layout එක ආරක්ෂා කරගන්න

        // Main Container Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header Panel (Top color bar)
        JPanel headerBar = new JPanel();
        headerBar.setBackground(TEAL_PRIMARY);
        headerBar.setPreferredSize(new Dimension(450, 10));
        mainPanel.add(headerBar, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 10, 5);

        // 1. Title
        JLabel titleLabel = new JLabel("Welcome Back", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 30, 0); // Title එකට යටින් ඉඩක් තැබීම
        formPanel.add(titleLabel, gbc);

        // 2. Email Label & Field
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 1;
        formPanel.add(emailLabel, gbc);

        emailField = new JTextField(20); // Field size එක fix කළා
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        emailField.setPreferredSize(new Dimension(0, 35)); // උස වැඩි කළා
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10) // Text එක අයිනටම නොවී තියෙන්න
        ));
        gbc.gridy = 2;
        formPanel.add(emailField, gbc);

        // 3. Password Label & Field
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 3;
        gbc.insets = new Insets(15, 5, 5, 5); // උඩින් පොඩි පරතරයක්
        formPanel.add(passLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        passwordField.setPreferredSize(new Dimension(0, 35));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 5, 5, 5);
        formPanel.add(passwordField, gbc);

        // 4. Login Button
        loginButton = new JButton("Login Now");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setBackground(TEAL_PRIMARY);
        loginButton.setForeground(Color.WHITE);
        loginButton.setPreferredSize(new Dimension(0, 45));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);

        gbc.gridy = 5;
        gbc.insets = new Insets(30, 5, 10, 5);
        formPanel.add(loginButton, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);

        // Cursor එක කෙලින්ම Email field එකට යනවා
        emailField.requestFocus();

        // Attach Controller
        new LoginController(this);
    }

    public String getEmail() { return emailField.getText().trim(); }
    public String getPassword() { return new String(passwordField.getPassword()); }
    public JButton getLoginButton() { return loginButton; }
}