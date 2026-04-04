package com.fot.system.main;

import com.fot.system.model.User;
import com.fot.system.view.dashboard.MainDashboard;

import javax.swing.*;

public class StudentApp {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            User student = new User() {};
            student.setId(13);
            student.setFirstName("Test");
            student.setLastName("Student");
            student.setRole("STUDENT");

            MainDashboard dashboard = new MainDashboard(student);
            dashboard.setVisible(true);
        });
    }
}