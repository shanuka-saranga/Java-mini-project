package com.fot.system.view.dashboard.student;

import com.fot.system.model.Course;
import com.fot.system.model.User;
import com.fot.system.service.CourseService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class MyCoursesPanel extends JPanel {

    private JPanel coursesContainer;

    public MyCoursesPanel(User user, String panelTitle) {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(new Color(245, 247, 250));
        mainContent.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel(panelTitle);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel coursesTitle = new JLabel("My Courses");
        coursesTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        coursesTitle.setBorder(new EmptyBorder(15, 0, 10, 0));
        coursesTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        coursesContainer = new JPanel(new WrapLayout(FlowLayout.LEFT, 20, 20));
        coursesContainer.setBackground(new Color(245, 247, 250));
        coursesContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel courseSection = new JPanel(new BorderLayout());
        courseSection.setBackground(new Color(245, 247, 250));
        courseSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        courseSection.add(coursesContainer, BorderLayout.NORTH);

        JScrollPane courseScroll = new JScrollPane(courseSection);
        courseScroll.setPreferredSize(new Dimension(900, 360));
        courseScroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        courseScroll.getVerticalScrollBar().setUnitIncrement(16);
        courseScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel noticesTitle = new JLabel("Notices");
        noticesTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        noticesTitle.setBorder(new EmptyBorder(20, 0, 10, 0));
        noticesTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel noticesPanel = createNoticesPanel();
        noticesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        mainContent.add(title);
        mainContent.add(coursesTitle);
        mainContent.add(courseScroll);
        mainContent.add(noticesTitle);
        mainContent.add(noticesPanel);

        JScrollPane mainScroll = new JScrollPane(mainContent);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);

        add(mainScroll, BorderLayout.CENTER);

        loadCourses();
    }

    private void loadCourses() {
        CourseService service = new CourseService();
        List<Course> list = service.getCourses();

        coursesContainer.removeAll();

        for (Course c : list) {
            coursesContainer.add(new CourseCard(c));
        }

        coursesContainer.revalidate();
        coursesContainer.repaint();
    }

    private JPanel createNoticesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(15, 15, 15, 15)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        panel.add(new JLabel("• Mid exam timetable will be released next week."));
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("• Assignment submissions close on Friday."));
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("• OOP practical lab starts at 1.00 PM tomorrow."));

        return panel;
    }
}