package com.fot.system.view.dashboard.admin.shared;

import com.fot.system.config.AppTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public abstract class  BaseAdminTablePanel extends JScrollPane {

    private final JTable table;
    private final DefaultTableModel tableModel;

    public BaseAdminTablePanel(String[] columns) {
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        styleTable();
        setViewportView(table);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(0, 121, 107), 1));
    }

    private void styleTable() {
        table.setRowHeight(45);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setGridColor(AppTheme.PRIMARY);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(AppTheme.TABLE_SELECTION_BG);
        table.setSelectionForeground(AppTheme.TEXT_DARK);

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(AppTheme.TABLE_HEADER_BG);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 50));
        table.getTableHeader().setReorderingAllowed(false);

        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable sourceTable, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(sourceTable, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);

                if (component instanceof JLabel) {
                    ((JLabel) component).setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                }

                if (!isSelected) {
                    component.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 252, 252));
                }

                return component;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
    }

    public void addRow(Object[] data) {
        tableModel.addRow(data);
    }

    public JTable getTable() {
        return table;
    }

    public DefaultTableModel getModel() {
        return tableModel;
    }
}
