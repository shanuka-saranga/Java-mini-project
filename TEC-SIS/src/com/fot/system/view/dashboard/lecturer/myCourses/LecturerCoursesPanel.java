package com.fot.system.view.dashboard.lecturer.myCourses;

import com.fot.system.config.AppTheme;
import com.fot.system.controller.AddCourseMaterialController;
import com.fot.system.controller.EditCourseMaterialController;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.service.CourseMaterialService;
import com.fot.system.service.CourseService;
import com.fot.system.service.FileOpenService;
import com.fot.system.view.components.CloseActionButton;
import com.fot.system.view.components.CustomButton;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class LecturerCoursesPanel extends JPanel {
    private static final String LIST_CARD = "LIST";
    private static final String DETAILS_CARD = "DETAILS";

    private final User currentUser;
    private final CourseService courseService;
    private final CourseMaterialService courseMaterialService;
    private final AddCourseMaterialController addCourseMaterialController;
    private final EditCourseMaterialController editCourseMaterialController;
    private final FileOpenService fileOpenService;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final JPanel courseListPanel;
    private final JPanel materialsListPanel;
    private final JLabel lblSelectedCourse;
    private final JLabel lblCourseMeta;
    private final JLabel lblCourseLecturer;
    private final JLabel lblCourseDepartment;
    private final JLabel lblOpenedCourseTab;
    private final CustomButton addNewButton;

    private List<Course> assignedCourses;
    private Course selectedCourse;

    public LecturerCoursesPanel(User user) {
        this.currentUser = user;
        this.courseService = new CourseService();
        this.courseMaterialService = new CourseMaterialService();
        this.addCourseMaterialController = new AddCourseMaterialController();
        this.editCourseMaterialController = new EditCourseMaterialController();
        this.fileOpenService = new FileOpenService();
        this.cardLayout = new CardLayout();
        this.cardPanel = new JPanel(cardLayout);

        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.SURFACE_SOFT);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        add(createHeader(), BorderLayout.NORTH);

        cardPanel.setOpaque(false);

        courseListPanel = new JPanel();
        courseListPanel.setOpaque(true);
        courseListPanel.setBackground(AppTheme.SURFACE_SOFT);
        courseListPanel.setLayout(new BoxLayout(courseListPanel, BoxLayout.Y_AXIS));
        JScrollPane courseListScrollPane = createScrollPane(courseListPanel);
        courseListScrollPane.getViewport().setBackground(AppTheme.SURFACE_SOFT);

        lblSelectedCourse = createTitleLabel("-");
        lblCourseMeta = createMetaLabel("-");
        lblCourseLecturer = createMetaLabel("-");
        lblCourseDepartment = createMetaLabel("-");

        lblOpenedCourseTab = new JLabel("Opened Course");
        lblOpenedCourseTab.setFont(AppTheme.fontBold(16));
        lblOpenedCourseTab.setForeground(AppTheme.TEXT_DARK);

        JPanel topActions = new JPanel();
        topActions.setOpaque(false);
        topActions.setLayout(new BoxLayout(topActions, BoxLayout.Y_AXIS));

        CloseActionButton closeButton = new CloseActionButton();
        closeButton.addActionListener(e -> cardLayout.show(cardPanel, LIST_CARD));
        closeButton.setAlignmentX(Component.RIGHT_ALIGNMENT);

        addNewButton = new CustomButton(
                "Add New",
                AppTheme.BTN_SAVE_BG,
                AppTheme.BTN_SAVE_FG,
                AppTheme.BTN_SAVE_HOVER,
                new Dimension(120, 40)
        );
        addNewButton.addActionListener(e -> openAddMaterialDialog());
        addNewButton.setAlignmentX(Component.RIGHT_ALIGNMENT);

        topActions.add(closeButton);
        topActions.add(Box.createVerticalStrut(10));
        topActions.add(addNewButton);

        materialsListPanel = new JPanel();
        materialsListPanel.setOpaque(false);
        materialsListPanel.setLayout(new BoxLayout(materialsListPanel, BoxLayout.Y_AXIS));

        JPanel detailsView = new JPanel(new BorderLayout(0, 16));
        detailsView.setOpaque(false);

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

        JPanel courseInfoPanel = new JPanel();
        courseInfoPanel.setOpaque(false);
        courseInfoPanel.setLayout(new BoxLayout(courseInfoPanel, BoxLayout.Y_AXIS));
        courseInfoPanel.add(lblSelectedCourse);
        courseInfoPanel.add(Box.createVerticalStrut(8));
        courseInfoPanel.add(lblCourseMeta);
        courseInfoPanel.add(Box.createVerticalStrut(6));
        courseInfoPanel.add(lblCourseLecturer);
        courseInfoPanel.add(Box.createVerticalStrut(6));
        courseInfoPanel.add(lblCourseDepartment);

        JLabel materialsHeading = new JLabel("Course Materials");
        materialsHeading.setFont(AppTheme.fontBold(18));
        materialsHeading.setForeground(AppTheme.TEXT_DARK);

        JLabel materialsSubtext = new JLabel("All current material cards for the selected course are shown below.");
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
        contentStack.add(courseInfoPanel);
        contentStack.add(Box.createVerticalStrut(22));
        contentStack.add(materialsHeader);
        contentStack.add(Box.createVerticalStrut(14));
        contentStack.add(materialsListPanel);
        contentStack.add(Box.createVerticalGlue());

        JScrollPane openedCourseScrollPane = createScrollPane(contentStack);
        openedCourseScrollPane.getViewport().setBackground(AppTheme.CARD_BG);

        openedCoursePanel.add(panelHeader, BorderLayout.NORTH);
        openedCoursePanel.add(openedCourseScrollPane, BorderLayout.CENTER);
        detailsView.add(openedCoursePanel, BorderLayout.CENTER);

        cardPanel.add(courseListScrollPane, LIST_CARD);
        cardPanel.add(detailsView, DETAILS_CARD);

        add(cardPanel, BorderLayout.CENTER);
        loadAssignedCourses();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 8));
        header.setOpaque(false);

        JLabel title = new JLabel("My Courses");
        title.setFont(AppTheme.fontBold(28));
        title.setForeground(AppTheme.TEXT_DARK);

        JLabel subtitle = new JLabel("Browse your assigned courses and open any course to manage its current materials.");
        subtitle.setFont(AppTheme.fontPlain(14));
        subtitle.setForeground(AppTheme.TEXT_SUBTLE);

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        return header;
    }

    private JScrollPane createScrollPane(JPanel content) {
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(AppTheme.CARD_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.fontBold(22));
        label.setForeground(AppTheme.TEXT_DARK);
        return label;
    }

    private JLabel createMetaLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.fontPlain(14));
        label.setForeground(AppTheme.TEXT_SUBTLE);
        return label;
    }

    private void loadAssignedCourses() {
        SwingWorker<List<Course>, Void> worker = new SwingWorker<List<Course>, Void>() {
            @Override
            protected List<Course> doInBackground() {
                return courseService.getCoursesByLecturerId(currentUser.getId());
            }

            @Override
            protected void done() {
                try {
                    assignedCourses = get();
                    renderCourseList();
                    cardLayout.show(cardPanel, LIST_CARD);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            LecturerCoursesPanel.this,
                            "Failed to load assigned courses.",
                            "Course Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void renderCourseList() {
        courseListPanel.removeAll();

        if (assignedCourses == null || assignedCourses.isEmpty()) {
            JLabel empty = new JLabel("No assigned courses available.");
            empty.setFont(AppTheme.fontPlain(14));
            empty.setForeground(AppTheme.TEXT_SUBTLE);
            empty.setBorder(new EmptyBorder(12, 8, 12, 8));
            courseListPanel.add(empty);
        } else {
            for (Course course : assignedCourses) {
                LecturerCourseCard card = new LecturerCourseCard(course, false);
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
        updateOpenedCourseTabTitle(course);
        lblSelectedCourse.setText(course.getCourseCode() + " - " + course.getCourseName());
        lblCourseMeta.setText("Session: " + valueOrDash(course.getSessionType()) + "  |  Credits: " + course.getCredits() + "  |  Hours: " + course.getTotalHours());
        lblCourseLecturer.setText("Lecturer: " + valueOrDash(course.getLecturerInChargeName()));
        lblCourseDepartment.setText("Department: " + valueOrDash(course.getDepartmentName()));
        addNewButton.setEnabled(true);
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
            renderMaterialsEmpty("No materials available for this course yet.");
            return;
        }

        for (CourseMaterial material : materials) {
            materialsListPanel.add(new CourseMaterialCard(
                    material,
                    () -> openMaterial(material),
                    () -> openEditMaterialDialog(material),
                    () -> deleteMaterial(material)
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

    private void openAddMaterialDialog() {
        if (selectedCourse == null) {
            return;
        }

        Window owner = SwingUtilities.getWindowAncestor(this);
        CourseMaterialFormDialog dialog = new CourseMaterialFormDialog(owner, "Add New Material", selectedCourse, null);
        dialog.setVisible(true);

        if (!dialog.isConfirmed()) {
            return;
        }

        try {
            addCourseMaterialController.addMaterial(new AddCourseMaterialRequest(
                    String.valueOf(selectedCourse.getId()),
                    selectedCourse.getCourseCode(),
                    dialog.getTitleValue(),
                    dialog.getDescriptionValue(),
                    dialog.getFilePathValue(),
                    dialog.getFileTypeValue(),
                    currentUser.getId()
            ));
            loadMaterialsForSelectedCourse();
            JOptionPane.showMessageDialog(this, "Course material added successfully!");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Material Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openMaterial(CourseMaterial material) {
        try {
            fileOpenService.openFile(material.getFilePath());
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Open Material", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openEditMaterialDialog(CourseMaterial material) {
        if (selectedCourse == null || material == null) {
            return;
        }

        Window owner = SwingUtilities.getWindowAncestor(this);
        CourseMaterialFormDialog dialog = new CourseMaterialFormDialog(owner, "Edit Material", selectedCourse, material);
        dialog.setVisible(true);

        if (!dialog.isConfirmed()) {
            return;
        }

        try {
            EditCourseMaterialRequest request = new EditCourseMaterialRequest(
                    material.getId(),
                    selectedCourse.getCourseCode(),
                    dialog.getTitleValue(),
                    dialog.getDescriptionValue(),
                    dialog.getFilePathValue(),
                    dialog.getFileTypeValue(),
                    currentUser.getId()
            );
            editCourseMaterialController.updateMaterial(request);
            loadMaterialsForSelectedCourse();
            JOptionPane.showMessageDialog(this, "Course material updated successfully!");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Material Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMaterial(CourseMaterial material) {
        if (material == null) {
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Remove \"" + material.getTitle() + "\" from this course?",
                "Remove Material",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            editCourseMaterialController.deleteMaterial(material.getId());
            loadMaterialsForSelectedCourse();
            JOptionPane.showMessageDialog(this, "Course material removed successfully!");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Material Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String valueOrDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }

    private void updateOpenedCourseTabTitle(Course course) {
        lblOpenedCourseTab.setText(course.getCourseCode() + " - " + course.getCourseName());
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
