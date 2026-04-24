package com.fot.system.view.dashboard.admin.manageCourses;

import com.fot.system.controller.AddCourseController;
import com.fot.system.config.AppTheme;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.service.CourseService;
import com.fot.system.view.components.CustomButton;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ManageCoursesPanel extends JPanel {
    private static final String DETAILS_CARD = "DETAILS";
    private static final String ADD_COURSE_CARD = "ADD_COURSE";
    private static final int EXPANDED_DIVIDER_SIZE = 5;
    private static final int COLLAPSED_DIVIDER_SIZE = 0;
    private static final int DETAILS_PANEL_HEIGHT = 350;

    private final CourseService courseService;
    private final CourseTablePanel courseTablePanel;
    private final CourseDetailsPanel courseDetailsPanel;
    private final AddNewCoursePanel addNewCoursePanel = new AddNewCoursePanel();
    private final AddCourseController addCourseController;
    private final CardLayout bottomCardLayout = new CardLayout();
    private final JPanel bottomContentPanel = new JPanel(bottomCardLayout);
    private JSplitPane splitPane;

    public ManageCoursesPanel(User currentUser) {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        add(createHeader(), BorderLayout.NORTH);

        courseTablePanel = new CourseTablePanel();
        courseDetailsPanel = new CourseDetailsPanel();
        courseDetailsPanel.setOnCloseAction(this::collapseBottomPanel);
        courseDetailsPanel.setOnCourseUpdatedAction(this::loadDataFromDatabase);
        courseDetailsPanel.setOnCourseDeletedAction(this::afterCourseDeleted);

        addNewCoursePanel.setOnCloseAction(this::collapseBottomPanel);
        courseService = new CourseService();
        addCourseController = new AddCourseController(addNewCoursePanel, this::afterCourseAdded);

        bottomContentPanel.add(courseDetailsPanel, DETAILS_CARD);
        bottomContentPanel.add(addNewCoursePanel, ADD_COURSE_CARD);

        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(courseTablePanel);
        splitPane.setBottomComponent(bottomContentPanel);
        splitPane.setResizeWeight(1.0);
        splitPane.setDividerSize(COLLAPSED_DIVIDER_SIZE);
        splitPane.setBackground(Color.WHITE);
        splitPane.setBorder(null);

        bottomContentPanel.setMinimumSize(new Dimension(0, 0));
        add(splitPane, BorderLayout.CENTER);

        courseTablePanel.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateDetailsView();
            }
        });

        loadDataFromDatabase();
        loadLookupData();
        SwingUtilities.invokeLater(this::collapseBottomPanel);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Course Management");
        title.setFont(AppTheme.fontBold(26));

        CustomButton addBtn = new CustomButton(
                "Add New Course",
                AppTheme.BTN_SAVE_BG,
                AppTheme.BTN_SAVE_FG,
                AppTheme.BTN_SAVE_HOVER,
                new Dimension(180, 40)
        );
        addBtn.setIcon(FontIcon.of(FontAwesomeSolid.BOOK_MEDICAL, 15, Color.WHITE));
        addBtn.addActionListener(e -> showAddCoursePanel());

        header.add(title, BorderLayout.WEST);
        header.add(addBtn, BorderLayout.EAST);
        return header;
    }

    private void loadDataFromDatabase() {
        SwingWorker<List<Course>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Course> doInBackground() {
                return courseService.getAllCourses();
            }

            @Override
            protected void done() {
                try {
                    List<Course> courses = get();
                    courseTablePanel.getModel().setRowCount(0);

                    for (Course course : courses) {
                        Object[] rowData = {
                                course.getId(),
                                course.getCourseCode(),
                                course.getCourseName(),
                                course.getDepartmentName(),
                                course.getCredits(),
                                course.getTotalHours(),
                                course.getSessionType(),
                                course.getNoOfQuizzes(),
                                course.getNoOfAssignments(),
                                course.getLecturerInChargeName() == null ? "-" : course.getLecturerInChargeName()
                        };
                        courseTablePanel.addRow(rowData);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error loading courses!");
                }
            }
        };
        worker.execute();
    }

    private void loadLookupData() {
        SwingWorker<LookupData, Void> worker = new SwingWorker<>() {
            @Override
            protected LookupData doInBackground() {
                return new LookupData(courseService.getAllDepartments(), courseService.getAllLecturers());
            }

            @Override
            protected void done() {
                try {
                    LookupData lookupData = get();
                    addNewCoursePanel.setDepartments(lookupData.departments);
                    addNewCoursePanel.setLecturers(lookupData.lecturers);
                    courseDetailsPanel.setDepartments(lookupData.departments);
                    courseDetailsPanel.setLecturers(lookupData.lecturers);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(
                            ManageCoursesPanel.this,
                            "Error loading course lookup data!",
                            "Course Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void showAddCoursePanel() {
        addNewCoursePanel.resetForm();
        bottomCardLayout.show(bottomContentPanel, ADD_COURSE_CARD);
        addNewCoursePanel.setVisible(true);
        courseDetailsPanel.setVisible(false);
        SwingUtilities.invokeLater(this::showBottomPanel);
    }

    private void updateDetailsView() {
        int row = courseTablePanel.getTable().getSelectedRow();
        if (row == -1) {
            return;
        }

        Course course = new Course();
        course.setId(Integer.parseInt(courseTablePanel.getModel().getValueAt(row, 0).toString()));
        course.setCourseCode(courseTablePanel.getModel().getValueAt(row, 1).toString());
        course.setCourseName(courseTablePanel.getModel().getValueAt(row, 2).toString());
        course.setDepartmentName(courseTablePanel.getModel().getValueAt(row, 3).toString());
        course.setCredits(Integer.parseInt(courseTablePanel.getModel().getValueAt(row, 4).toString()));
        course.setTotalHours(Integer.parseInt(courseTablePanel.getModel().getValueAt(row, 5).toString()));
        course.setSessionType(courseTablePanel.getModel().getValueAt(row, 6).toString());
        course.setNoOfQuizzes(Integer.parseInt(courseTablePanel.getModel().getValueAt(row, 7).toString()));
        course.setNoOfAssignments(Integer.parseInt(courseTablePanel.getModel().getValueAt(row, 8).toString()));
        String lecturerName = courseTablePanel.getModel().getValueAt(row, 9).toString();
        course.setLecturerInChargeName("-".equals(lecturerName) ? null : lecturerName);

        SwingWorker<Course, Void> worker = new SwingWorker<>() {
            @Override
            protected Course doInBackground() {
                return courseService.getCourseByCode(course.getCourseCode());
            }

            @Override
            protected void done() {
                try {
                    Course fullCourse = get();
                    bottomCardLayout.show(bottomContentPanel, DETAILS_CARD);
                    courseDetailsPanel.updateDetails(fullCourse);
                    SwingUtilities.invokeLater(ManageCoursesPanel.this::showBottomPanel);
                    courseDetailsPanel.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void showBottomPanel() {
        splitPane.setDividerSize(EXPANDED_DIVIDER_SIZE);
        bottomContentPanel.setPreferredSize(new Dimension(0, DETAILS_PANEL_HEIGHT));
        bottomContentPanel.revalidate();

        int availableHeight = splitPane.getHeight();
        if (availableHeight > DETAILS_PANEL_HEIGHT) {
            splitPane.setDividerLocation(availableHeight - DETAILS_PANEL_HEIGHT);
        } else {
            splitPane.setDividerLocation(0.6);
        }
        splitPane.revalidate();
        splitPane.repaint();
    }

    private void collapseBottomPanel() {
        bottomContentPanel.setPreferredSize(new Dimension(0, 0));
        splitPane.setDividerSize(COLLAPSED_DIVIDER_SIZE);
        splitPane.setDividerLocation(1.0);
        splitPane.revalidate();
        splitPane.repaint();
    }

    private void afterCourseAdded() {
        loadDataFromDatabase();
        collapseBottomPanel();
    }

    private void afterCourseDeleted() {
        loadDataFromDatabase();
        collapseBottomPanel();
    }

    private static class LookupData {
        private final List<Department> departments;
        private final List<Staff> lecturers;

        private LookupData(List<Department> departments, List<Staff> lecturers) {
            this.departments = departments;
            this.lecturers = lecturers;
        }
    }
}
