package com.fot.system.controller;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.service.UserService;
import com.fot.system.view.dashboard.MainDashboard;
import com.fot.system.view.login.LoginView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController {
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    private final LoginView view;
    private final UserService service;

    /**
     * initialize login controller
     * @param view login view
     * @author methum
     */
    public LoginController(LoginView view ) {
        this.view = view;
        this.service = new UserService();
        this.view.getLoginButton().addActionListener(new LoginAction());
    }

    /**
     * handle login action
     * @author methum
     */
    class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            final String loginEmail = view.getEmail();
            final String loginPassword = view.getPassword();
            view.getLoginButton().setEnabled(false);

            SwingWorker<User, Void> worker = new SwingWorker<>() {
                @Override
                protected User doInBackground() {
                    return service.login(loginEmail, loginPassword);
                }

                @Override
                protected void done() {
                    try {
                        User user = get();
                        if (user != null) {
                            openDashboard(user);
                            view.dispose();
                        } else {
                            JOptionPane.showMessageDialog(view, // popup window
                                    "Invalid Email or Password",
                                    "Login Failed",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                        LOGGER.log(Level.SEVERE, "Login process failed", cause);
                        JOptionPane.showMessageDialog(view,
                                "Login failed. Please try again.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    } finally {
                        view.getLoginButton().setEnabled(true);
                    }
                }
            };
            worker.execute();
        }
    }

    /**
     * open dashboard after successful login
     * @param user logged in user
     * @author methum
     */
    private void openDashboard(User user) {
        MainDashboard dashboard = new MainDashboard(user);
        dashboard.setVisible(true);
    }
}
