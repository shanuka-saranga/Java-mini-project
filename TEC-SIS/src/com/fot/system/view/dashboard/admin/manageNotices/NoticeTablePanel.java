package com.fot.system.view.dashboard.admin.manageNotices;

import com.fot.system.view.dashboard.admin.components.BaseAdminTablePanel;
import javax.swing.*;
import javax.swing.table.TableColumnModel;

/**
 * render notice listing table with tuned column sizing
 * @author janith
 */
public class NoticeTablePanel extends BaseAdminTablePanel {

    /**
     * initialize notice table panel
     * @author janith
     */
    public NoticeTablePanel() {
        super(new String[]{"ID", "Title", "Audience", "Priority", "Status", "Published", "Expires"});
        configureTableSizing();
    }

    /**
     * apply default table behavior and column widths
     * @author janith
     */
    private void configureTableSizing() {
        JTable table = getTable();
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        TableColumnModel columns = table.getColumnModel();
        columns.getColumn(0).setPreferredWidth(60);
        columns.getColumn(1).setPreferredWidth(300);
        columns.getColumn(2).setPreferredWidth(120);
        columns.getColumn(3).setPreferredWidth(110);
        columns.getColumn(4).setPreferredWidth(110);
        columns.getColumn(5).setPreferredWidth(140);
        columns.getColumn(6).setPreferredWidth(140);
    }
}
