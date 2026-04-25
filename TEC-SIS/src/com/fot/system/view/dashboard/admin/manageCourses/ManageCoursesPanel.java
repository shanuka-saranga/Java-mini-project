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

/**
 * manage course dashboard section with table and detail forms
 * @author janith
 */
public class ManageCoursesPanel extends JPanel {
    private static final String DETAILS_CARD = "DETAILS";
    private static final String ADD_COURSE_CARD = "ADD_COURSE";
    private static final int EXPANDED_DIVIDER_SIZE = 5;
    private static final int COLLAPSED_DIVIDER_SIZE = 0;
    private static final double DETAILS_PANEL_RATIO = 0.60;

    private final CourseService courseService;
    private final CourseTablePanel courseTablePanel;
    private final CourseDetailsPanel courseDetailsPanel;
    private final AddNewCoursePanel addNewCoursePanel = new AddNewCoursePanel();
    private final AddCourseController addCourseController;
    private final CardLayout bottomCardLayout = new CardLayout();
    private final JPanel bottomContentPanel = new JPanel(bottomCardLayout);
    private JSplitPane splitPane;
    private boolean bottomExpanded;
    private boolean selectionLocked;
    private int lockedSelectionRow = -1;
    private boolean restoringSelection;
    private boolean initialLoadPending = true;

    /**
     * initialize manage courses panel
     * @param currentUser logged in user
     * @author janith
     */
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
        courseDetailsPanel.setOnEditModeChangedAction(this::onEditModeChanged);

        addNewCoursePanel.setOnCloseAction(this::collapseBottomPanel);
        courseService = new CourseService();
        addCourseController = new AddCourseController(addNewCoursePanel, this::afterCourseAdded);

        bottomContentPanel.add(courseDetailsPanel, DETAILS_CARD);
        bottomContentPanel.add(addNewCoursePanel, ADD_COURSE_CARD);

        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(courseTablePanel);
        splitPane.setBottomComponent(bottomContentPanel);
        splitPane.setResizeWeight(DETAILS_PANEL_RATIO);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerSize(COLLAPSED_DIVIDER_SIZE);
        splitPane.setBackground(Color.WHITE);
        splitPane.setBorder(null);

        courseTablePanel.setMinimumSize(new Dimension(0, 140));
        bottomContentPanel.setMinimumSize(new Dimension(0, 0));
        add(splitPane, BorderLayout.CENTER);

        courseTablePanel.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || restoringSelection) {
                return;
            }
            if (initialLoadPending) {
                return;
            }

            if (selectionLocked) {
                maintainLockedSelection();
                return;
            }

            if (courseTablePanel.getTable().getSelectedRow() != -1) {
                updateDetailsView();
            }
        });

        loadDataFromDatabase();
        loadLookupData();
        SwingUtilities.invokeLater(this::collapseBottomPanel);
    }

    /**
     * create panel header with title and add button
     * @author janith
     */
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

    /**
     * load course table data from database
     * @author janith
     */
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

                    if (initialLoadPending) {
                        JTable table = courseTablePanel.getTable();
                        table.clearSelection();
                        selectionLocked = false;
                        lockedSelectionRow = -1;
                        courseDetailsPanel.setVisible(false);
                        collapseBottomPanel();
                        initialLoadPending = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error loading courses!");
                }
            }
        };
        worker.execute();
    }

    /**
     * load department and lecturer lookup data
     * @author janith
     */
    private void loadLookupData() {
        SwingWorker<ManageCoursesLookupData, Void> worker = new SwingWorker<>() {
            @Override
            protected ManageCoursesLookupData doInBackground() {
                return new ManageCoursesLookupData(courseService.getAllDepartments(), courseService.getAllLecturers());
            }

            @Override
            protected void done() {
                try {
                    ManageCoursesLookupData lookupData = get();
                    addNewCoursePanel.setDepartments(lookupData.getDepartments());
                    addNewCoursePanel.setLecturers(lookupData.getLecturers());
                    courseDetailsPanel.setDepartments(lookupData.getDepartments());
                    courseDetailsPanel.setLecturers(lookupData.getLecturers());
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

    /**
     * show add new course form
     * @author janith
     */
    private void showAddCoursePanel() {
        addNewCoursePanel.resetForm();
        bottomCardLayout.show(bottomContentPanel, ADD_COURSE_CARD);
        addNewCoursePanel.setVisible(true);
        courseDetailsPanel.setVisible(false);
        SwingUtilities.invokeLater(this::showBottomPanel);
    }

    /**
     * load selected row course details view
     * @author janith
     */
    private void updateDetailsView() {
        JTable table = courseTablePanel.getTable();
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            return;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);

        Course course = new Course();
        course.setId(Integer.parseInt(courseTablePanel.getModel().getValueAt(modelRow, 0).toString()));
        course.setCourseCode(courseTablePanel.getModel().getValueAt(modelRow, 1).toString());
        course.setCourseName(courseTablePanel.getModel().getValueAt(modelRow, 2).toString());
        course.setDepartmentName(courseTablePanel.getModel().getValueAt(modelRow, 3).toString());
        course.setCredits(Integer.parseInt(courseTablePanel.getModel().getValueAt(modelRow, 4).toString()));
        course.setTotalHours(Integer.parseInt(courseTablePanel.getModel().getValueAt(modelRow, 5).toString()));
        course.setSessionType(courseTablePanel.getModel().getValueAt(modelRow, 6).toString());
        course.setNoOfQuizzes(Integer.parseInt(courseTablePanel.getModel().getValueAt(modelRow, 7).toString()));
        course.setNoOfAssignments(Integer.parseInt(courseTablePanel.getModel().getValueAt(modelRow, 8).toString()));
        String lecturerName = courseTablePanel.getModel().getValueAt(modelRow, 9).toString();
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

    /**
     * expand bottom section with active card
     * @author janith
     */
    private void showBottomPanel() {
        if (bottomExpanded) {
            splitPane.setDividerSize(EXPANDED_DIVIDER_SIZE);
            splitPane.revalidate();
            splitPane.repaint();
            return;
        }

        bottomExpanded = true;
        splitPane.setDividerSize(EXPANDED_DIVIDER_SIZE);
        splitPane.setDividerLocation(DETAILS_PANEL_RATIO);
        splitPane.revalidate();
        splitPane.repaint();
    }

    /**
     * collapse bottom section
     * @author janith
     */
    private void collapseBottomPanel() {
        bottomExpanded = false;
        splitPane.setDividerSize(COLLAPSED_DIVIDER_SIZE);
        splitPane.setDividerLocation(1.0);
        splitPane.revalidate();
        splitPane.repaint();
    }

    /**
     * refresh table after add action
     * @author janith
     */
    private void afterCourseAdded() {
        loadDataFromDatabase();
        collapseBottomPanel();
    }

    /**
     * refresh table after delete action
     * @author janith
     */
    private void afterCourseDeleted() {
        loadDataFromDatabase();
        collapseBottomPanel();
    }

    /**
     * toggle course table selection lock in edit mode
     * @param editing edit mode status
     * @author janith
     */
    private void onEditModeChanged(boolean editing) {
        JTable table = courseTablePanel.getTable();
        if (editing) {
            selectionLocked = true;
            lockedSelectionRow = table.getSelectedRow();
            return;
        }

        selectionLocked = false;
        lockedSelectionRow = table.getSelectedRow();
    }

    /**
     * keep current row selected while edit mode lock is active
     * @author janith
     */
    private void maintainLockedSelection() {
        JTable table = courseTablePanel.getTable();
        int selectedRow = table.getSelectedRow();

        if (lockedSelectionRow == -1) {
            return;
        }
        if (selectedRow == lockedSelectionRow) {
            return;
        }

        restoringSelection = true;
        try {
            table.getSelectionModel().setSelectionInterval(lockedSelectionRow, lockedSelectionRow);
        } finally {
            restoringSelection = false;
        }
    }
}
