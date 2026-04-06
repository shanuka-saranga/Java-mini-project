package com.fot.system.view.dashboard.admin;

import com.fot.system.config.AppTheme;
import com.fot.system.model.User;
import com.fot.system.service.UserService;
import com.fot.system.view.dashboard.admin.UserTableComponent;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageUsersPanel extends JPanel {

    private JTable userTable;
    private UserTableComponent userTableComp;
    private DefaultTableModel tableModel;
    private JPanel detailsPanel;
    private JLabel lblDetailName, lblDetailEmail, lblDetailRole, lblDetailStatus;
    private UserDetailsComponent userDetailsComp;
    private final UserService userService ;
    JSplitPane splitPane ;

    public ManageUsersPanel(User currentUser) {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        add(createHeader(), BorderLayout.NORTH);
        userTableComp = new UserTableComponent();
        userDetailsComp = new UserDetailsComponent();
        userService = new UserService();
        loadDataFromDatabase();

        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(userTableComp);
        splitPane.setBottomComponent(userDetailsComp);

        splitPane.setDividerLocation(350);
        splitPane.setDividerSize(5);
        splitPane.setBackground(Color.WHITE);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);

        add(createActionButtons(), BorderLayout.SOUTH);

        userTableComp.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateDetailsView();
            }
        });
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

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("User Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        JButton addBtn = new JButton("Add New User");
        addBtn.setBackground(AppTheme.PRIMARY);
        addBtn.setForeground(Color.WHITE);
        addBtn.setIcon(FontIcon.of(FontAwesomeSolid.USER_PLUS, 15, Color.WHITE));
        addBtn.setPreferredSize(new Dimension(160, 40));
        header.add(title, BorderLayout.WEST);
        header.add(addBtn, BorderLayout.EAST);
        return header;
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
                        userDetailsComp.updateDetails(fullUser);
                        splitPane.setDividerLocation(350);
                        userDetailsComp.setVisible(true);
                    } catch (Exception e) { e.printStackTrace(); }
                }
            };
            worker.execute();
        }
    }

    private JPanel createActionButtons() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionPanel.setOpaque(false);

        JButton editBtn = new JButton("Edit User");
        editBtn.setIcon(FontIcon.of(FontAwesomeSolid.USER_EDIT, 14));

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setBackground(new Color(220, 53, 69));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setIcon(FontIcon.of(FontAwesomeSolid.TRASH_ALT, 14, Color.WHITE));

        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);
        return actionPanel;
    }
}