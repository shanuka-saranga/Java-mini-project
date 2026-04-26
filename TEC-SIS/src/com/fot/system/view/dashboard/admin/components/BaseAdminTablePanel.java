package com.fot.system.view.dashboard.admin.components;

import com.fot.system.config.AppTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * base reusable admin table panel with shared styling
 * @author janith
 */
public abstract class  BaseAdminTablePanel extends JScrollPane {

    private final JTable table;
    private final DefaultTableModel tableModel;

    /**
     * initialize base admin table panel
     * @param columns table column names
     * @author janith
     */
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
        setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, false));
    }

    /**
     * apply common table styles for admin tables
     * @author janith
     */
    private void styleTable() {
        table.setRowHeight(45);
        table.setFont(AppTheme.fontPlain(14));
        table.setGridColor(AppTheme.BORDER_SOFT);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(AppTheme.TABLE_SELECTION_BG);
        table.setSelectionForeground(AppTheme.TABLE_SELECTION_FG);

        table.getTableHeader().setFont(AppTheme.fontBold(14));
        table.getTableHeader().setBackground(AppTheme.TABLE_HEADER_BG);
        table.getTableHeader().setForeground(AppTheme.TABLE_HEADER_FG);
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
                    component.setBackground(row % 2 == 0 ? AppTheme.TABLE_ROW_BG : AppTheme.TABLE_ROW_ALT_BG);
                }

                return component;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
    }

    /**
     * append row into table model
     * @param data row values
     * @author janith
     */
    public void addRow(Object[] data) {
        tableModel.addRow(data);
    }

    /**
     * get table instance
     * @author janith
     */
    public JTable getTable() {
        return table;
    }

    /**
     * get table model instance
     * @author janith
     */
    public DefaultTableModel getModel() {
        return tableModel;
    }
}
