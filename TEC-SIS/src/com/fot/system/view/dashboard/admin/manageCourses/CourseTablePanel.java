package com.fot.system.view.dashboard.admin.manageCourses;

import com.fot.system.config.AppTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CourseTablePanel extends JScrollPane {

    private final JTable courseTable;
    private final DefaultTableModel tableModel;

    public CourseTablePanel() {
        String[] columns = {"ID", "Code", "Course Name", "Department", "Credits", "Hours", "Session", "Lecturer"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        courseTable = new JTable(tableModel);
        styleTable();
        setViewportView(courseTable);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
    }

    private void styleTable() {
        courseTable.setRowHeight(45);
        courseTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        courseTable.setGridColor(AppTheme.PRIMARY);
        courseTable.setShowVerticalLines(false);

        courseTable.setSelectionBackground(AppTheme.TABLE_SELECTION_BG);
        courseTable.setSelectionForeground(AppTheme.TEXT_DARK);

        courseTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        courseTable.getTableHeader().setBackground(AppTheme.TABLE_HEADER_BG);
        courseTable.getTableHeader().setForeground(Color.WHITE);
        courseTable.getTableHeader().setPreferredSize(new Dimension(0, 50));
        courseTable.getTableHeader().setReorderingAllowed(false);

        ((DefaultTableCellRenderer) courseTable.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
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

        for (int i = 0; i < courseTable.getColumnCount(); i++) {
            courseTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        setBorder(BorderFactory.createLineBorder(new Color(0, 121, 107), 1));
    }

    public void addRow(Object[] data) {
        tableModel.addRow(data);
    }

    public JTable getTable() {
        return courseTable;
    }

    public DefaultTableModel getModel() {
        return tableModel;
    }
}
