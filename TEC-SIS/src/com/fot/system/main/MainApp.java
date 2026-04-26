package com.fot.system.main;

import com.fot.system.config.AppTheme;
import com.fot.system.view.login.LoginView;

import javax.swing.*;

public class MainApp {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            AppTheme.applyGlobalTheme();

            LoginView loginView = new LoginView("", "");
            loginView.setVisible(true);
        });
    }
}
