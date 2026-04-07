package com.fot.system.view.dashboard.admin.manageNotices;

import com.fot.system.config.AppTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class NoticeTablePanel extends JScrollPane {

    private final JTable noticeTable;
    private final DefaultTableModel tableModel;

    public NoticeTablePanel() {
        String[] columns = {"ID", "Title", "Audience", "Priority", "Status", "Published", "Expires"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        noticeTable = new JTable(tableModel);
        styleTable();
        setViewportView(noticeTable);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
    }

    private void styleTable() {
        noticeTable.setRowHeight(45);
        noticeTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        noticeTable.setGridColor(AppTheme.PRIMARY);
        noticeTable.setShowVerticalLines(false);
        noticeTable.setSelectionBackground(AppTheme.TABLE_SELECTION_BG);
        noticeTable.setSelectionForeground(AppTheme.TEXT_DARK);

        noticeTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        noticeTable.getTableHeader().setBackground(AppTheme.TABLE_HEADER_BG);
        noticeTable.getTableHeader().setForeground(Color.WHITE);
        noticeTable.getTableHeader().setPreferredSize(new Dimension(0, 50));
        noticeTable.getTableHeader().setReorderingAllowed(false);

        ((DefaultTableCellRenderer) noticeTable.getTableHeader().getDefaultRenderer())
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

        for (int i = 0; i < noticeTable.getColumnCount(); i++) {
            noticeTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        setBorder(BorderFactory.createLineBorder(new Color(0, 121, 107), 1));
    }

    public void addRow(Object[] data) {
        tableModel.addRow(data);
    }

    public JTable getTable() {
        return noticeTable;
    }

    public DefaultTableModel getModel() {
        return tableModel;
    }
}
