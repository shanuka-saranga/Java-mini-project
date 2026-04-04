package com.fot.system.controller;

import com.fot.system.model.User;
import com.fot.system.service.UserService;
import com.fot.system.view.dashboard.MainDashboard;
import com.fot.system.view.login.LoginView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginController {

    private final LoginView view;
    private final UserService service;
    String email;
    String password;

    public LoginController(LoginView view , String email, String password) {
        this.view = view;
        this.service = new UserService();
        this.view.getLoginButton().addActionListener(new LoginAction());

        // TODO: Remove hardcoded email and password
        this.email = email;
        this.password = password;
    }

    class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
//                String email = view.getEmail(); TODO
//                String password = view.getPassword(); TODO

                User user = service.login(email, password);

                if (user != null) {
                    view.showSuccessMessage("Welcome " + user.getFullName());
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
}