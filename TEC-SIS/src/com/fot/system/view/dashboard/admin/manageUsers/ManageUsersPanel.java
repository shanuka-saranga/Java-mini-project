package com.fot.system.view.dashboard.admin.manageUsers;

import com.fot.system.controller.AddUserController;
import com.fot.system.config.AppTheme;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.service.DepartmentService;
import com.fot.system.service.UserService;
import com.fot.system.view.components.CustomButton;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageUsersPanel extends JPanel {
    private static final String DETAILS_CARD = "DETAILS";
    private static final String ADD_USER_CARD = "ADD_USER";
    private static final int EXPANDED_DIVIDER_SIZE = 5;
    private static final int COLLAPSED_DIVIDER_SIZE = 0;

    private JTable userTable;
    private UserTablePanel userTableComp;
    private DefaultTableModel tableModel;
    private JPanel detailsPanel;
    private JLabel lblDetailName, lblDetailEmail, lblDetailRole, lblDetailStatus;
    private UserDetailsPanel userDetailsComp;
    private final UserService userService ;
    private final DepartmentService departmentService;
    private final AddNewUserPanel addNewUserPanel = new AddNewUserPanel();
    private final AddUserController addUserController;
    private final CardLayout bottomCardLayout = new CardLayout();
    private final JPanel bottomContentPanel = new JPanel(bottomCardLayout);
    private static final int DETAILS_PANEL_HEIGHT = 350;
    JSplitPane splitPane ;

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
        addNewUserPanel.setOnCloseAction(this::collapseBottomPanel);
        userService = new UserService();
        departmentService = new DepartmentService();
        addUserController = new AddUserController(addNewUserPanel, this::afterUserAdded);
        loadDataFromDatabase();
        loadDepartments();

        bottomContentPanel.add(userDetailsComp, DETAILS_CARD);
        bottomContentPanel.add(addNewUserPanel, ADD_USER_CARD);

        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(userTableComp);
        splitPane.setBottomComponent(bottomContentPanel);
        splitPane.setResizeWeight(1.0);
        splitPane.setDividerSize(COLLAPSED_DIVIDER_SIZE);
        splitPane.setBackground(Color.WHITE);
        splitPane.setBorder(null);

        bottomContentPanel.setMinimumSize(new Dimension(0, 0));

        add(splitPane, BorderLayout.CENTER);

        userTableComp.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateDetailsView();
            }
        });

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
                    userTableComp.getModel().setRowCount(0); // UI එක update කරන්නේ මෙතනදී

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
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error loading data!");
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
                    e.printStackTrace();
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
        addBtn.setIcon(FontIcon.of(FontAwesomeSolid.USER_PLUS, 15, Color.WHITE));
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
        int row = userTableComp.getTable().getSelectedRow();
        if (row != -1) {
            int userId = Integer.parseInt(userTableComp.getModel().getValueAt(row, 0).toString());

            SwingWorker<User, Void> worker = new SwingWorker<>() {
                @Override
                protected User doInBackground() {
                    return userService.getUserById(userId);
                }

                @Override
                protected void done() {
                    try {
                        User fullUser = get();
                        bottomCardLayout.show(bottomContentPanel, DETAILS_CARD);
                        userDetailsComp.updateDetails(fullUser);
                        SwingUtilities.invokeLater(ManageUsersPanel.this::showBottomPanel);
                        userDetailsComp.setVisible(true);
                    } catch (Exception e) { e.printStackTrace(); }
                }
            };
            worker.execute();
        }
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

    private void afterUserAdded() {
        loadDataFromDatabase();
        collapseBottomPanel();
    }

    private void afterUserDeleted() {
        loadDataFromDatabase();
        collapseBottomPanel();
    }


}
