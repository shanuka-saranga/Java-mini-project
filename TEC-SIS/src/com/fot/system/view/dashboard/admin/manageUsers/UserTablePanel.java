package com.fot.system.view.dashboard.admin.manageUsers;

import com.fot.system.config.AppTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UserTablePanel extends JScrollPane {

    private JTable userTable;
    private DefaultTableModel tableModel;

    public UserTablePanel() {
        String[] columns = {"ID", "Name", "Email", "Role", "Status"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(tableModel);
        styleTable();
        setViewportView(userTable);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
    }

    private void styleTable() {
        userTable.setRowHeight(45);
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userTable.setGridColor(AppTheme.PRIMARY);
        userTable.setShowVerticalLines(false);

        userTable.setSelectionBackground(AppTheme.TABLE_SELECTION_BG);
        userTable.setSelectionForeground(AppTheme.TEXT_DARK);

        userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        userTable.getTableHeader().setBackground(AppTheme.TABLE_HEADER_BG);
        userTable.getTableHeader().setForeground(Color.WHITE);
        userTable.getTableHeader().setPreferredSize(new Dimension(0, 50));
        userTable.getTableHeader().setReorderingAllowed(false);

        ((DefaultTableCellRenderer)userTable.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                setHorizontalAlignment(JLabel.CENTER);

                if (c instanceof JLabel) {
                    ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                }

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 252, 252));
                }

                return c;
            }
        };

        for (int i = 0; i < userTable.getColumnCount(); i++) {
            userTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        this.setBorder(BorderFactory.createLineBorder(new Color(0, 121, 107), 1));
    }

    public void addRow(Object[] data) {
        tableModel.addRow(data);
    }

    public JTable getTable() {
        return userTable;
    }

    public DefaultTableModel getModel() {
        return tableModel;
    }
}