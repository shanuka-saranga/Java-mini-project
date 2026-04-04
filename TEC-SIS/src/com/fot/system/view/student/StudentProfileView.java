package com.fot.system.view.student;

import com.fot.system.config.AppTheme;

import javax.swing.*;
import java.awt.*;

public class StudentProfileView extends JPanel {

    public StudentProfileView(int studentId) {
        setLayout(new BorderLayout());
        setBackground(AppTheme.CONTENT_BG);

        JLabel label = new JLabel("Student Profile View");
        label.setFont(AppTheme.TITLE_FONT);
        label.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(label, BorderLayout.NORTH);
    }
}
