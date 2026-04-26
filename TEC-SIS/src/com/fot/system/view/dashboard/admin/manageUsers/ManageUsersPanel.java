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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;

/**
 * manage admin user table interactions and details/edit panel flow
 * @author janith
 */
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
    private final TableRowSorter<DefaultTableModel> userTableSorter;
    private JTextField txtSearch;

    /**
     * initialize manage users view and wire table/detail events
     * @param currentUser current logged-in user
     * @author janith
     */
    public ManageUsersPanel(User currentUser) {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        add(createHeader(), BorderLayout.NORTH);
        userTableComp = new UserTablePanel();
        userTableSorter = new TableRowSorter<>(userTableComp.getModel());
        userTableComp.getTable().setRowSorter(userTableSorter);
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

    /**
     * load all users and refresh table data without changing ui structure
     * @author janith
     */
    private void loadDataFromDatabase() {
        final Integer selectedUserId = getSelectedUserId();
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
                        return;
                    }

                    if (selectedUserId != null && restoreSelectionByUserId(selectedUserId)) {
                        return;
                    }

                    JTable table = userTableComp.getTable();
                    if (table.getSelectedRow() == -1) {
                        userDetailsComp.clearDetails();
                        collapseBottomPanel();
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

    /**
     * load department list for add and edit user forms
     * @author janith
     */
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

    /**
     * build header with title, search input and add user action
     * @author janith
     */
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

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(320, 40));
        txtSearch.setFont(AppTheme.fontPlain(14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, false),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applySearchFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applySearchFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applySearchFilter();
            }
        });

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centerPanel.setOpaque(false);
        centerPanel.add(txtSearch);

        header.add(title, BorderLayout.WEST);
        header.add(centerPanel, BorderLayout.CENTER);
        header.add(addBtn, BorderLayout.EAST);
        return header;
    }

    /**
     * apply table row filtering across all visible columns
     * @author janith
     */
    private void applySearchFilter() {
        if (userTableSorter == null || userTableComp == null) {
            return;
        }

        String keyword = txtSearch == null ? "" : txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            userTableSorter.setRowFilter(null);
            return;
        }

        userTableSorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(keyword)));

        JTable table = userTableComp.getTable();
        if (table.getSelectedRow() == -1) {
            selectionLocked = false;
            lockedSelectionRow = -1;
            userDetailsComp.clearDetails();
            collapseBottomPanel();
        }
    }

    /**
     * show add-user form card in lower panel
     * @author janith
     */
    private void showAddUserPanel() {
        addNewUserPanel.resetForm();
        bottomCardLayout.show(bottomContentPanel, ADD_USER_CARD);
        addNewUserPanel.setVisible(true);
        userDetailsComp.setVisible(false);
        SwingUtilities.invokeLater(this::showBottomPanel);
    }

    /**
     * load and show selected user details card
     * @author janith
     */
    private void updateDetailsView() {
        JTable table = userTableComp.getTable();
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            userDetailsComp.clearDetails();
            collapseBottomPanel();
            return;
        }

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
                        userDetailsComp.clearDetails();
                        collapseBottomPanel();
                        return;
                    }
                    bottomCardLayout.show(bottomContentPanel, DETAILS_CARD);
                    userDetailsComp.updateDetails(fullUser);
                    SwingUtilities.invokeLater(ManageUsersPanel.this::showBottomPanel);
                    userDetailsComp.setVisible(true);
                } catch (Exception ignored) {
                    userDetailsComp.clearDetails();
                    collapseBottomPanel();
                }
            }
        };
        worker.execute();
    }

    /**
     * expand lower split region for details/add form
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
     * collapse lower split region and keep table focused
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
     * refresh table after user add and collapse lower region
     * @author janith
     */
    private void afterUserAdded() {
        loadDataFromDatabase();
        collapseBottomPanel();
    }

    /**
     * refresh table after user delete and collapse lower region
     * @author janith
     */
    private void afterUserDeleted() {
        loadDataFromDatabase();
        collapseBottomPanel();
    }

    /**
     * lock current row while edit mode is active
     * @param editing current edit-mode state from details panel
     * @author janith
     */
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

    /**
     * keep selection fixed to locked row while editing
     * @author janith
     */
    private void maintainLockedSelection() {
        JTable table = userTableComp.getTable();
        int selectedRow = table.getSelectedRow();
        int rowCount = table.getRowCount();

        if (lockedSelectionRow == -1) {
            return;
        }
        if (lockedSelectionRow >= rowCount) {
            selectionLocked = false;
            lockedSelectionRow = -1;
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

    /**
     * resolve selected user id from current table selection
     * @author janith
     */
    private Integer getSelectedUserId() {
        JTable table = userTableComp.getTable();
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return null;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        Object idValue = userTableComp.getModel().getValueAt(modelRow, 0);
        if (idValue == null) {
            return null;
        }

        try {
            return Integer.parseInt(idValue.toString());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    /**
     * restore selection to a user row by id after table refresh
     * @param userId target user id
     * @author janith
     */
    private boolean restoreSelectionByUserId(int userId) {
        JTable table = userTableComp.getTable();
        DefaultTableModel model = userTableComp.getModel();

        for (int modelRow = 0; modelRow < model.getRowCount(); modelRow++) {
            Object value = model.getValueAt(modelRow, 0);
            if (value == null) {
                continue;
            }
            if (!String.valueOf(userId).equals(value.toString())) {
                continue;
            }

            int viewRow = table.convertRowIndexToView(modelRow);
            if (viewRow < 0) {
                return false;
            }

            restoringSelection = true;
            try {
                table.getSelectionModel().setSelectionInterval(viewRow, viewRow);
            } finally {
                restoringSelection = false;
            }

            updateDetailsView();
            return true;
        }

        return false;
    }
}
