package com.fot.system.view.dashboard.student;

import com.fot.system.config.AppTheme;
import com.fot.system.model.entity.*;
import com.fot.system.service.CourseMaterialService;
import com.fot.system.service.CourseService;
import com.fot.system.service.FileOpenService;
import com.fot.system.service.NoticeService;
import com.fot.system.view.components.CloseActionButton;
import com.fot.system.view.components.CourseSummaryCard;
import com.fot.system.view.dashboard.admin.components.NoticeFeedPanel;
import com.fot.system.view.dashboard.lecturer.myCourses.CourseMaterialCard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class StudentHomePanel extends JPanel {
    private static final String LIST_CARD = "LIST";
    private static final String DETAILS_CARD = "DETAILS";

    private final User currentUser;
    private final CourseService courseService;
    private final CourseMaterialService courseMaterialService;
    private final NoticeService noticeService;
    private final FileOpenService fileOpenService;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final JPanel courseListPanel;
    private final JPanel materialsListPanel;
    private final NoticeFeedPanel noticeFeedPanel;
    private final JLabel lblOpenedCourseTab;

    private Course selectedCourse;
    private List<Course> studentCourses;

    public StudentHomePanel(User user) {
        this.currentUser = user;
        this.courseService = new CourseService();
        this.courseMaterialService = new CourseMaterialService();
        this.noticeService = new NoticeService();
        this.fileOpenService = new FileOpenService();
        this.cardLayout = new CardLayout();
        this.cardPanel = new JPanel(cardLayout);

        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.SURFACE_SOFT);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        add(createHeader(), BorderLayout.NORTH);

        cardPanel.setOpaque(false);

        courseListPanel = new JPanel();
        courseListPanel.setBackground(AppTheme.SURFACE_SOFT);
        courseListPanel.setLayout(new BoxLayout(courseListPanel, BoxLayout.Y_AXIS));

        noticeFeedPanel = new NoticeFeedPanel("Student Notices");
        noticeFeedPanel.setPreferredSize(new Dimension(360, 0));

        JScrollPane courseListScrollPane = createScrollPane(courseListPanel, AppTheme.SURFACE_SOFT);
        JPanel listView = createListView(courseListScrollPane);

        lblOpenedCourseTab = new JLabel("Opened Course");
        lblOpenedCourseTab.setFont(AppTheme.fontBold(16));
        lblOpenedCourseTab.setForeground(AppTheme.TEXT_DARK);

        materialsListPanel = new JPanel();
        materialsListPanel.setOpaque(false);
        materialsListPanel.setLayout(new BoxLayout(materialsListPanel, BoxLayout.Y_AXIS));

        cardPanel.add(listView, LIST_CARD);
        cardPanel.add(createDetailsView(), DETAILS_CARD);

        add(cardPanel, BorderLayout.CENTER);
        loadStudentCourses();
        loadStudentNotices();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 8));
        header.setOpaque(false);

        JLabel title = new JLabel("My Courses");
        title.setFont(AppTheme.fontBold(28));
        title.setForeground(AppTheme.TEXT_DARK);

        JLabel subtitle = new JLabel("Open a course to view all active course materials shared for your learning.");
        subtitle.setFont(AppTheme.fontPlain(14));
        subtitle.setForeground(AppTheme.TEXT_SUBTLE);

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        return header;
    }

    private JPanel createListView(JScrollPane courseListScrollPane) {
        JPanel listView = new JPanel(new BorderLayout(18, 0));
        listView.setOpaque(false);

        JPanel noticesWrap = new JPanel(new BorderLayout());
        noticesWrap.setOpaque(false);
        noticesWrap.setPreferredSize(new Dimension(360, 0));
        noticesWrap.add(noticeFeedPanel, BorderLayout.CENTER);

        JPanel coursesWrap = new JPanel(new BorderLayout(0, 12));
        coursesWrap.setOpaque(false);
        coursesWrap.add(courseListScrollPane, BorderLayout.CENTER);

        listView.add(coursesWrap, BorderLayout.CENTER);
        listView.add(noticesWrap, BorderLayout.EAST);
        return listView;
    }

    private JPanel createDetailsView() {
        JPanel detailsView = new JPanel(new BorderLayout(0, 16));
        detailsView.setOpaque(false);

        JPanel topActions = new JPanel();
        topActions.setOpaque(false);
        topActions.setLayout(new BoxLayout(topActions, BoxLayout.Y_AXIS));

        CloseActionButton closeButton = new CloseActionButton();
        closeButton.addActionListener(e -> cardLayout.show(cardPanel, LIST_CARD));
        closeButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        topActions.add(closeButton);

        JPanel openedCoursePanel = new JPanel(new BorderLayout(0, 16));
        openedCoursePanel.setBackground(AppTheme.CARD_BG);
        openedCoursePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, true),
                new EmptyBorder(22, 22, 22, 22)
        ));

        JPanel panelHeader = new JPanel(new BorderLayout(12, 0));
        panelHeader.setOpaque(false);

        JPanel tabLabelWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabLabelWrap.setOpaque(false);
        tabLabelWrap.add(lblOpenedCourseTab);

        panelHeader.add(tabLabelWrap, BorderLayout.WEST);
        panelHeader.add(topActions, BorderLayout.EAST);

        JLabel materialsHeading = new JLabel("Course Materials");
        materialsHeading.setFont(AppTheme.fontBold(18));
        materialsHeading.setForeground(AppTheme.TEXT_DARK);

        JLabel materialsSubtext = new JLabel("All active materials for the selected course are shown below.");
        materialsSubtext.setFont(AppTheme.fontPlain(14));
        materialsSubtext.setForeground(AppTheme.TEXT_SUBTLE);

        JPanel materialsHeader = new JPanel();
        materialsHeader.setOpaque(false);
        materialsHeader.setLayout(new BoxLayout(materialsHeader, BoxLayout.Y_AXIS));
        materialsHeader.add(materialsHeading);
        materialsHeader.add(Box.createVerticalStrut(6));
        materialsHeader.add(materialsSubtext);

        JPanel contentStack = new JPanel();
        contentStack.setOpaque(false);
        contentStack.setLayout(new BoxLayout(contentStack, BoxLayout.Y_AXIS));
        contentStack.add(materialsHeader);
        contentStack.add(Box.createVerticalStrut(14));
        contentStack.add(materialsListPanel);
        contentStack.add(Box.createVerticalGlue());

        JScrollPane openedCourseScrollPane = createScrollPane(contentStack, AppTheme.CARD_BG);
        openedCoursePanel.add(panelHeader, BorderLayout.NORTH);
        openedCoursePanel.add(openedCourseScrollPane, BorderLayout.CENTER);
        detailsView.add(openedCoursePanel, BorderLayout.CENTER);

        return detailsView;
    }

    private JScrollPane createScrollPane(JPanel content, Color viewportColor) {
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(viewportColor);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private void loadStudentCourses() {
        SwingWorker<List<Course>, Void> worker = new SwingWorker<List<Course>, Void>() {
            @Override
            protected List<Course> doInBackground() {
                return courseService.getCoursesByStudentUserId(currentUser.getId());
            }

            @Override
            protected void done() {
                try {
                    studentCourses = get();
                    renderCourseList();
                    cardLayout.show(cardPanel, LIST_CARD);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            StudentHomePanel.this,
                            "Failed to load student courses.",
                            "Course Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void loadStudentNotices() {
        SwingWorker<List<Notice>, Void> worker = new SwingWorker<List<Notice>, Void>() {
            @Override
            protected List<Notice> doInBackground() {
                return noticeService.getRecentVisibleNoticesForRole(currentUser.getRole(), 10);
            }

            @Override
            protected void done() {
                try {
                    noticeFeedPanel.setNotices(get());
                } catch (Exception e) {
                    noticeFeedPanel.setNotices(java.util.Collections.emptyList());
                }
            }
        };
        worker.execute();
    }

    private void renderCourseList() {
        courseListPanel.removeAll();

        if (studentCourses == null || studentCourses.isEmpty()) {
            JLabel empty = new JLabel("No courses are available for your student record yet.");
            empty.setFont(AppTheme.fontPlain(14));
            empty.setForeground(AppTheme.TEXT_SUBTLE);
            empty.setBorder(new EmptyBorder(12, 8, 12, 8));
            courseListPanel.add(empty);
        } else {
            for (Course course : studentCourses) {
                CourseSummaryCard card = new CourseSummaryCard(course, false);
                MouseAdapter openCourseHandler = new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        openCourse(course);
                    }
                };
                attachClickHandler(card, openCourseHandler);
                courseListPanel.add(card);
                courseListPanel.add(Box.createVerticalStrut(12));
            }
        }

        courseListPanel.revalidate();
        courseListPanel.repaint();
    }

    private void openCourse(Course course) {
        selectedCourse = course;
        lblOpenedCourseTab.setText(course.getCourseCode() + " - " + course.getCourseName());
        loadMaterialsForSelectedCourse();
        cardLayout.show(cardPanel, DETAILS_CARD);
    }

    private void loadMaterialsForSelectedCourse() {
        if (selectedCourse == null) {
            renderMaterialsEmpty("Select a course to view materials.");
            return;
        }

        SwingWorker<List<CourseMaterial>, Void> worker = new SwingWorker<List<CourseMaterial>, Void>() {
            @Override
            protected List<CourseMaterial> doInBackground() {
                return courseMaterialService.getMaterialsByCourseId(selectedCourse.getId());
            }

            @Override
            protected void done() {
                try {
                    renderMaterials(get());
                } catch (Exception e) {
                    renderMaterialsEmpty("Unable to load course materials.");
                }
            }
        };
        worker.execute();
    }

    private void renderMaterials(List<CourseMaterial> materials) {
        materialsListPanel.removeAll();

        if (materials == null || materials.isEmpty()) {
            renderMaterialsEmpty("No materials are available for this course yet.");
            return;
        }

        for (CourseMaterial material : materials) {
            materialsListPanel.add(new CourseMaterialCard(
                    material,
                    () -> openMaterial(material)
            ));
            materialsListPanel.add(Box.createVerticalStrut(12));
        }

        materialsListPanel.revalidate();
        materialsListPanel.repaint();
    }

    private void renderMaterialsEmpty(String message) {
        materialsListPanel.removeAll();
        JLabel empty = new JLabel(message);
        empty.setFont(AppTheme.fontPlain(14));
        empty.setForeground(AppTheme.TEXT_SUBTLE);
        empty.setBorder(new EmptyBorder(12, 8, 12, 8));
        materialsListPanel.add(empty);
        materialsListPanel.revalidate();
        materialsListPanel.repaint();
    }

    private void openMaterial(CourseMaterial material) {
        try {
            fileOpenService.openFile(material.getFilePath());
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Open Material", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void attachClickHandler(Component component, MouseAdapter adapter) {
        component.addMouseListener(adapter);
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                attachClickHandler(child, adapter);
            }
        }
    }
}
