package com.fot.system.view.dashboard.admin.manageUsers;

import com.fot.system.controller.AddUserController;
import com.fot.system.config.AppTheme;
import com.fot.system.model.entity.Department;
import com.fot.system.model.entity.User;
import com.fot.system.service.DepartmentService;
import com.fot.system.service.UserService;
import com.fot.system.view.components.CustomButton;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ManageUsersPanel extends JPanel {
    private static final String DETAILS_CARD = "DETAILS";
    private static final String ADD_USER_CARD = "ADD_USER";
    private static final int EXPANDED_DIVIDER_SIZE = 5;
    private static final int COLLAPSED_DIVIDER_SIZE = 0;
    private static final double DETAILS_PANEL_RATIO = 0.60;

    private final UserTablePanel userTableComp;
    private final UserDetailsPanel userDetailsComp;
    private final UserService userService;
    private final DepartmentService departmentService;
    private final AddNewUserPanel addNewUserPanel = new AddNewUserPanel();
    private final AddUserController addUserController;
    private final CardLayout bottomCardLayout = new CardLayout();
    private final JPanel bottomContentPanel = new JPanel(bottomCardLayout);
    private JSplitPane splitPane;
    private boolean bottomExpanded;
    private boolean selectionLocked;
    private int lockedSelectionRow = -1;
    private boolean restoringSelection;
    private boolean initialLoadPending = true;

    public ManageUsersPanel(User currentUser) {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        add(createHeader(), BorderLayout.NORTH);
        userTableComp = new UserTablePanel();
        userDetailsComp = new UserDetailsPanel();
        userDetailsComp.setOnCloseAction(this::collapseBottomPanel);
        userDetailsComp.setOnUserUpdatedAction(this::loadDataFromDatabase);
        userDetailsComp.setOnUserDeletedAction(this::afterUserDeleted);
        userDetailsComp.setOnEditModeChangedAction(this::onEditModeChanged);
        addNewUserPanel.setOnCloseAction(this::collapseBottomPanel);
        userService = new UserService();
        departmentService = new DepartmentService();
        addUserController = new AddUserController(addNewUserPanel, this::afterUserAdded);

        bottomContentPanel.add(userDetailsComp, DETAILS_CARD);
        bottomContentPanel.add(addNewUserPanel, ADD_USER_CARD);

        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(userTableComp);
        splitPane.setBottomComponent(bottomContentPanel);
        splitPane.setResizeWeight(DETAILS_PANEL_RATIO);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerSize(COLLAPSED_DIVIDER_SIZE);
        splitPane.setBackground(Color.WHITE);
        splitPane.setBorder(null);

        userTableComp.setMinimumSize(new Dimension(0, 140));
        bottomContentPanel.setMinimumSize(new Dimension(0, 0));

        add(splitPane, BorderLayout.CENTER);

        userTableComp.getTable().getSelectionModel().addListSelectionListener(e -> {
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

            if (userTableComp.getTable().getSelectedRow() != -1) {
                updateDetailsView();
            }
        });

        loadDataFromDatabase();
        loadDepartments();
        SwingUtilities.invokeLater(this::collapseBottomPanel);
    }

    private void loadDataFromDatabase() {
        SwingWorker<List<User>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<User> doInBackground() {
                return userService.getAllUsers();
            }

            @Override
            protected void done() {
                try {
                    List<User> userList = get();
                    userTableComp.getModel().setRowCount(0);

                    for (User user : userList) {
                        Object[] rowData = {
                                user.getId(),
                                user.getFirstName() + " " + user.getLastName(),
                                user.getEmail(),
                                user.getRole(),
                                user.getStatus()
                        };
                        userTableComp.addRow(rowData);
                    }

                    if (initialLoadPending) {
                        JTable table = userTableComp.getTable();
                        table.clearSelection();
                        selectionLocked = false;
                        lockedSelectionRow = -1;
                        userDetailsComp.setVisible(false);
                        collapseBottomPanel();
                        initialLoadPending = false;
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            ManageUsersPanel.this,
                            "Error loading users!",
                            "User Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void loadDepartments() {
        SwingWorker<List<Department>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Department> doInBackground() {
                return departmentService.getAllDepartments();
            }

            @Override
            protected void done() {
                try {
                    List<Department> departments = get();
                    addNewUserPanel.setDepartments(departments);
                    userDetailsComp.setDepartments(departments);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            ManageUsersPanel.this,
                            "Error loading departments!",
                            "Department Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("User Management");
        title.setFont(AppTheme.fontBold(26));
        CustomButton addBtn = new CustomButton(
                "Add New User",
                AppTheme.BTN_SAVE_BG,
                AppTheme.BTN_SAVE_FG,
                AppTheme.BTN_SAVE_HOVER,
                new Dimension(160, 40)
        );
        addBtn.setIcon(FontIcon.of(FontAwesomeSolid.USER_PLUS, 15, AppTheme.BTN_SAVE_FG));
        addBtn.setPreferredSize(new Dimension(160, 40));
        addBtn.addActionListener(e -> showAddUserPanel());
        header.add(title, BorderLayout.WEST);
        header.add(addBtn, BorderLayout.EAST);
        return header;
    }

    private void showAddUserPanel() {
        addNewUserPanel.resetForm();
        bottomCardLayout.show(bottomContentPanel, ADD_USER_CARD);
        addNewUserPanel.setVisible(true);
        userDetailsComp.setVisible(false);
        SwingUtilities.invokeLater(this::showBottomPanel);
    }

    private void updateDetailsView() {
        JTable table = userTableComp.getTable();
        int viewRow = table.getSelectedRow();
        if (viewRow != -1) {
            int modelRow = table.convertRowIndexToModel(viewRow);
            int userId = Integer.parseInt(userTableComp.getModel().getValueAt(modelRow, 0).toString());

            SwingWorker<User, Void> worker = new SwingWorker<>() {
                @Override
                protected User doInBackground() {
                    return userService.getUserById(userId);
                }

                @Override
                protected void done() {
                    try {
                        User fullUser = get();
                        if (fullUser == null) {
                            return;
                        }
                        bottomCardLayout.show(bottomContentPanel, DETAILS_CARD);
                        userDetailsComp.updateDetails(fullUser);
                        SwingUtilities.invokeLater(ManageUsersPanel.this::showBottomPanel);
                        userDetailsComp.setVisible(true);
                    } catch (Exception ignored) {
                    }
                }
            };
            worker.execute();
        }
    }

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

    private void collapseBottomPanel() {
        bottomExpanded = false;
        splitPane.setDividerSize(COLLAPSED_DIVIDER_SIZE);
        splitPane.setDividerLocation(1.0);
        splitPane.revalidate();
        splitPane.repaint();
    }

    private void afterUserAdded() {
        loadDataFromDatabase();
        collapseBottomPanel();
    }

    private void afterUserDeleted() {
        loadDataFromDatabase();
        collapseBottomPanel();
    }

    private void onEditModeChanged(boolean editing) {
        JTable table = userTableComp.getTable();
        if (editing) {
            selectionLocked = true;
            lockedSelectionRow = table.getSelectedRow();
            return;
        }

        selectionLocked = false;
        lockedSelectionRow = table.getSelectedRow();
    }

    private void maintainLockedSelection() {
        JTable table = userTableComp.getTable();
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
