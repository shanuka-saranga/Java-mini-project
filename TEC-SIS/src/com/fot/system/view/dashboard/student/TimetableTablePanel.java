package com.fot.system.view.dashboard.student;

import com.fot.system.model.TimetableEntry;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TimetableTablePanel extends JPanel {

    private final Color cardColor = Color.WHITE;
    private final Color textDark = new Color(30, 30, 30);

    private JLabel selectedDayLabel;
    private JTable timetableTable;
    private DefaultTableModel tableModel;

    public TimetableTablePanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(cardColor);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        selectedDayLabel = new JLabel("Monday Schedule");
        selectedDayLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        selectedDayLabel.setForeground(textDark);

        String[] columns = {"Time", "Course", "Venue", "Lecturer"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        timetableTable = new JTable(tableModel);
        timetableTable.setRowHeight(30);
        timetableTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        timetableTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(timetableTable);

        add(selectedDayLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setDaySchedule(String day, List<TimetableEntry> entries) {
        selectedDayLabel.setText(day + " Schedule");
        tableModel.setRowCount(0);

        if (entries == null || entries.isEmpty()) {
            tableModel.addRow(new Object[]{"-", "No lectures scheduled", "-", "-"});
            return;
        }

        for (TimetableEntry entry : entries) {
            tableModel.addRow(new Object[]{
                    entry.getTimeRange(),
                    entry.getCourseName(),
                    entry.getVenue(),
                    entry.getLecturer()
            });
        }
    }
}