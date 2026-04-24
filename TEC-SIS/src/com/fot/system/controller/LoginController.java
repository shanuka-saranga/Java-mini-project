package com.fot.system.controller;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.service.UserService;
import com.fot.system.view.dashboard.MainDashboard;
import com.fot.system.view.login.LoginView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginController {

    private final LoginView view;
    private final UserService service;
    private final String email;
    private final String password;

    public LoginController(LoginView view , String email, String password) {
        this.view = view;
        this.service = new UserService();
        this.email = email;
        this.password = password;
        this.view.getLoginButton().addActionListener(new LoginAction());
    }

    class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String loginEmail = isBlank(email) ? view.getEmail() : email;
                String loginPassword = isBlank(password) ? view.getPassword() : password;
                User user = service.login(loginEmail, loginPassword);

                if (user != null) {
                    openDashboard(user);
                    view.dispose();
                } else {
                    JOptionPane.showMessageDialog(view,
                            "Invalid Email or Password",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openDashboard(User user) {

        String role = user.getRole();
        MainDashboard dashboard = new MainDashboard(user);
        dashboard.setVisible(true);

        switch (role) {

            case "Admin":
                System.out.println("Open Admin Dashboard");
                break;

            case "Student":
                System.out.println("Open Student Dashboard");
                break;

            case "Lecturer":
                System.out.println("Open Lecturer Dashboard");
                break;

            case "TO":
                System.out.println("Open Technical Officer Dashboard");
                break;

            case "Dean":
                System.out.println("Open Dean Dashboard");
                break;

            default:
                System.out.println("Unknown role");
        }

    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
