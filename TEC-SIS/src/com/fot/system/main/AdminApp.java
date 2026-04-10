package com.fot.system.main;

import com.fot.system.view.login.LoginView;

import javax.swing.*;

public class AdminApp {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            LoginView loginView = new LoginView("admin@tec.ruh.ac.lk","1234");
            loginView.setVisible(true);
        });
    }
}
