package com.fot.system.view.dashboard.admin.manageCourses;

import com.fot.system.view.dashboard.admin.components.BaseAdminTablePanel;

import javax.swing.*;
import javax.swing.table.TableColumnModel;

public class CourseTablePanel extends BaseAdminTablePanel {

    public CourseTablePanel() {
        super(new String[]{"ID", "Code", "Course Name", "Department", "Credits", "Hours", "Session", "Quizzes", "Assignments", "Lecturer"});
        configureTableSizing();
    }

    private void configureTableSizing() {
        JTable table = getTable();
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        TableColumnModel columns = table.getColumnModel();
        columns.getColumn(0).setPreferredWidth(55);
        columns.getColumn(1).setPreferredWidth(110);
        columns.getColumn(2).setPreferredWidth(240);
        columns.getColumn(3).setPreferredWidth(180);
        columns.getColumn(4).setPreferredWidth(70);
        columns.getColumn(5).setPreferredWidth(80);
        columns.getColumn(6).setPreferredWidth(120);
        columns.getColumn(7).setPreferredWidth(90);
        columns.getColumn(8).setPreferredWidth(110);
        columns.getColumn(9).setPreferredWidth(220);
    }
}
