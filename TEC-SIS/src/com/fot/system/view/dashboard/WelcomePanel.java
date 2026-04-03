package com.fot.system.view.dashboard;

import com.fot.system.model.User;
import javax.swing.*;
import java.awt.*;

public class WelcomePanel extends JPanel {
    public WelcomePanel(User user) {
        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());

        JLabel welcomeMsg = new JLabel("Welcome back, " + user.getFirstName() + "!");
        welcomeMsg.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeMsg.setForeground(new Color(0, 128, 128));

        add(welcomeMsg);
    }
}