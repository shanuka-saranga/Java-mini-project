package com.fot.system.main;

import com.fot.system.config.AppTheme;
import com.fot.system.view.login.LoginView;

import javax.swing.*;

public class AdminApp {

    public static void main(String[] args) {

        // Thread safe
        SwingUtilities.invokeLater(() -> {
            AppTheme.applyGlobalTheme();
            LoginView loginView = new LoginView("admin@tec.ruh.ac.lk","1234");
            loginView.setVisible(true);
        });
    }
}
