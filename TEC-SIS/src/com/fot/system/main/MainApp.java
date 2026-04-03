package com.fot.system.main;

import com.fot.system.view.login.LoginView;

import javax.swing.*;

public class MainApp {

    public static void main(String[] args) {

        // Ensure UI runs on Event Dispatch Thread (BEST PRACTICE)
        SwingUtilities.invokeLater(() -> {
            try {
                // Optional: Set system look & feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Launch Login Screen
            LoginView loginView = new LoginView();
            loginView.setVisible(true);
        });
    }
}