package com.fot.system.main;

import com.fot.system.view.login.LoginView;

import javax.swing.*;

public class StudentApp {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            LoginView loginView = new LoginView("aruni@fot.ruh.ac.lk", "pass123");
            loginView.setVisible(true);
        });
    }
}