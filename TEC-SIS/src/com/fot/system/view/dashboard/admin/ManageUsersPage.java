package com.fot.system.view.dashboard.admin;

import com.fot.system.config.AppTheme;
import com.fot.system.model.User;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManageUsersPage extends JPanel {

    private JTable userTable;
    private DefaultTableModel tableModel;

    public ManageUsersPage(User user) {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // 1. Header Section (Title + Add Button)
        add(createHeader(), BorderLayout.NORTH);

        // 2. Center Section (The Table)
        add(createTableSection(), BorderLayout.CENTER);

        // 3. Bottom Section (Update & Delete Buttons)
        add(createActionButtons(), BorderLayout.SOUTH);
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

        // Add User Logic
        addBtn.addActionListener(e -> {
            // මෙතනදී "Add User" Dialog එක open කරන්න ඕනේ
            JOptionPane.showMessageDialog(this, "Opening Add User Form...");
        });

        header.add(title, BorderLayout.WEST);
        header.add(addBtn, BorderLayout.EAST);
        return header;
    }

    private JScrollPane createTableSection() {
        String[] columns = {"User ID", "Full Name", "Email", "Role", "Department"};
        tableModel = new DefaultTableModel(columns, 0);
        userTable = new JTable(tableModel);

        // Table Styling
        userTable.setRowHeight(35);
        userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Sample Data (පසුව Database එකෙන් ගමු)
        tableModel.addRow(new Object[]{"ST001", "Janith Dilshan", "janith@fot.ruh.ac.lk", "Student", "ICT"});
        tableModel.addRow(new Object[]{"AD001", "Admin User", "admin@tec.ruh.ac.lk", "Admin", "Dean Office"});

        return new JScrollPane(userTable);
    }

    private JPanel createActionButtons() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionPanel.setOpaque(false);

        JButton updateBtn = new JButton("Update Selected");
        updateBtn.setIcon(FontIcon.of(FontAwesomeSolid.EDIT, 14, Color.BLACK));

        JButton deleteBtn = new JButton("Delete User");
        deleteBtn.setBackground(new Color(220, 53, 69)); // රතු පාට
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setIcon(FontIcon.of(FontAwesomeSolid.TRASH_ALT, 14, Color.WHITE));

        // Delete Logic
        deleteBtn.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row != -1) {
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this user?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    tableModel.removeRow(row);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a user to delete!");
            }
        });

        actionPanel.add(updateBtn);
        actionPanel.add(deleteBtn);
        return actionPanel;
    }
}