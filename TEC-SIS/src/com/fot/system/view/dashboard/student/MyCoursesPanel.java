package com.fot.system.view.dashboard.student;

import com.fot.system.model.Course;
import com.fot.system.model.User;
import com.fot.system.service.CourseService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MyCoursesPanel extends JPanel {

    private JPanel container;

    public MyCoursesPanel(User user, String panelTitle) {

        setLayout(new BorderLayout());

        JLabel title = new JLabel(panelTitle);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 0));

        add(title, BorderLayout.NORTH);

        container = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        container.setBackground(new Color(245, 247, 250));

        JScrollPane scroll = new JScrollPane(container);
        scroll.setBorder(null);

        add(scroll, BorderLayout.CENTER);

        loadCourses();
    }

    private void loadCourses() {
        CourseService service = new CourseService();
        List<Course> list = service.getCourses();

        for (Course c : list) {
            container.add(new CourseCard(c));
        }

        container.revalidate();
        container.repaint();
    }
}