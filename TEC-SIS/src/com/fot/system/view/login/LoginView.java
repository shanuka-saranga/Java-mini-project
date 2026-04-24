package com.fot.system.view.login;

import com.fot.system.config.AppTheme;
import com.fot.system.controller.LoginController;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginView extends JFrame {

    private JTextField emailField = new JTextField(20);
    private JPasswordField passwordField = new JPasswordField(20);
    private JButton loginButton = new JButton("Login Now");

    public LoginView(String email, String password) {
        setTitle("TEC-SIS | Login");
        setSize(450, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Color.WHITE);

        main.add(header(), BorderLayout.NORTH);
        main.add(form(email, password), BorderLayout.CENTER);

        add(main);

        (emailField.getText().isEmpty() ? emailField : passwordField).requestFocus();

        new LoginController(this, email, password);
    }

    private JPanel header() {
        JPanel p = new JPanel();
        p.setBackground(AppTheme.PRIMARY);
        p.setPreferredSize(new Dimension(450, 10));
        return p;
    }

    private JPanel form(String email, String password) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(10, 5, 10, 5);
        g.gridx = 0;
        g.gridwidth = 2;

        p.add(title("Welcome Back"), set(g, 0, 30));

        p.add(label("Email Address"), set(g, 1, 5));
        styleField(emailField);
        emailField.setText(email == null ? "" : email);
        p.add(emailField, set(g, 2, 5));

        p.add(label("Password"), set(g, 3, 15));
        styleField(passwordField);
        passwordField.setText(password == null ? "" : password);
        p.add(passwordField, set(g, 4, 5));

        styleButton(loginButton);
        addFadeEffect(loginButton); // 🔥 subtle fade (no design change)
        p.add(loginButton, set(g, 5, 30));

        return p;
    }

    private GridBagConstraints set(GridBagConstraints g, int y, int top) {
        g.gridy = y;
        g.insets = new Insets(top, 5, 5, 5);
        return g;
    }

    private JLabel title(String text) {
        JLabel l = new JLabel(text, JLabel.CENTER);
        l.setFont(new Font("Segoe UI", Font.BOLD, 26));
        return l;
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return l;
    }

    private void styleField(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        f.setPreferredSize(new Dimension(0, 35));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void styleButton(JButton b) {
        b.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b.setBackground(AppTheme.PRIMARY);
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(200, 45));
    }

    // 🔥 Fade effect WITHOUT changing color
    private void addFadeEffect(JButton button) {
        Color original = AppTheme.PRIMARY;

        button.addMouseListener(new java.awt.event.MouseAdapter() {

            Timer fadeIn, fadeOut;
            float alpha = 1.0f;

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (fadeOut != null && fadeOut.isRunning()) fadeOut.stop();

                fadeIn = new Timer(20, null);
                fadeIn.addActionListener(ev -> {
                    alpha = Math.max(0.85f, alpha - 0.05f);
                    button.setBackground(new Color(
                            original.getRed(),
                            original.getGreen(),
                            original.getBlue(),
                            (int)(alpha * 255)
                    ));
                    if (alpha <= 0.85f) fadeIn.stop();
                });
                fadeIn.start();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (fadeIn != null && fadeIn.isRunning()) fadeIn.stop();

                fadeOut = new Timer(20, null);
                fadeOut.addActionListener(ev -> {
                    alpha = Math.min(1.0f, alpha + 0.05f);
                    button.setBackground(new Color(
                            original.getRed(),
                            original.getGreen(),
                            original.getBlue(),
                            (int)(alpha * 255)
                    ));
                    if (alpha >= 1.0f) fadeOut.stop();
                });
                fadeOut.start();
            }
        });
    }

    public String getEmail() { return emailField.getText().trim(); }
    public String getPassword() { return new String(passwordField.getPassword()); }
    public JButton getLoginButton() { return loginButton; }

    public void showSuccessMessage(String msg) {
        JDialog d = new JOptionPane(msg, JOptionPane.INFORMATION_MESSAGE)
                .createDialog(this, "Success");

        new Timer(1500, e -> d.dispose()).start();
        d.setVisible(true);
    }
}