package com.fot.system.view.dashboard.student;

import com.fot.system.model.Course;

import javax.swing.*;
import java.awt.*;

public class CourseCard extends JPanel {

    public CourseCard(Course course) {

        setPreferredSize(new Dimension(250, 140));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(220,220,220)));
        setLayout(new BorderLayout());

        JLabel code = new JLabel(course.getCourseCode());
        code.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel name = new JLabel("<html>"+course.getCourseName()+"</html>");

        JPanel top = new JPanel(new GridLayout(2,1));
        top.setOpaque(false);
        top.add(code);
        top.add(name);

        JPanel bottom = new JPanel(new GridLayout(1,3));
        bottom.setOpaque(false);

        bottom.add(new JLabel("Cr: "+course.getCredits()));
        bottom.add(new JLabel("Hr: "+course.getTotalHours()));
        bottom.add(new JLabel(course.getSessionType()));

        add(top, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }
}