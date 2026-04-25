package com.fot.system.view.dashboard.admin.manageUsers;

import com.fot.system.view.dashboard.admin.components.BaseAdminTablePanel;

import javax.swing.*;
import javax.swing.table.TableColumnModel;

/**
 * render user listing table with tuned column sizing
 * @author janith
 */
public class UserTablePanel extends BaseAdminTablePanel {

    /**
     * initialize user table panel
     * @author janith
     */
    public UserTablePanel() {
        super(new String[]{"ID", "Name", "Email", "Role", "Status"});
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
        columns.getColumn(0).setPreferredWidth(70);
        columns.getColumn(1).setPreferredWidth(220);
        columns.getColumn(2).setPreferredWidth(280);
        columns.getColumn(3).setPreferredWidth(130);
        columns.getColumn(4).setPreferredWidth(120);
    }
}
