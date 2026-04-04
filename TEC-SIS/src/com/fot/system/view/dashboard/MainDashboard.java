package com.fot.system.view.dashboard;

import com.fot.system.config.AppConfig;
import com.fot.system.model.User;
import com.fot.system.view.dashboard.admin.AdminHomePanel;
import com.fot.system.view.dashboard.admin.ManageUsersPanel;
import com.fot.system.view.dashboard.sidebar.AdminSidebar;
import com.fot.system.view.dashboard.sidebar.BaseSidebar;

import javax.swing.*;
import java.awt.*;

public class MainDashboard extends JFrame {

    private BaseSidebar sidebar;
    private JPanel contentArea;
    private CardLayout cardLayout;
    private User currentUser;

    public MainDashboard(User user) {
        this.currentUser = user;

        setTitle("TEC-SIS | " + user.getRole() + " Dashboard");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        if (AppConfig.ROLE_ADMIN.equalsIgnoreCase(user.getRole())) {
            sidebar = new AdminSidebar(this);
        } else if (AppConfig.ROLE_STUDENT.equalsIgnoreCase(user.getRole())) {
//            sidebar = new StudentSidebar(this); TODO
        } else {
//            sidebar = new LecturerSidebar(this); TODO
        }
        add(sidebar, BorderLayout.WEST);
        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(Color.WHITE);
        contentArea.add(new AdminHomePanel(user), AppConfig.MENU_HOME);
        contentArea.add(new ManageUsersPanel(user),AppConfig.MENU_MANAGE_USERS);
        add(contentArea, BorderLayout.CENTER);


    }

    public void switchPanel(String cardName) {
        cardLayout.show(contentArea, cardName);
    }


}