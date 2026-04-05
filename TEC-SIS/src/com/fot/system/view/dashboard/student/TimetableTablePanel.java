package com.fot.system.view.dashboard.student;

import com.fot.system.model.TimetableEntry;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class TimetableTablePanel extends JPanel {

    private final Color cardColor = Color.WHITE;
    private final Color textDark = new Color(30, 30, 30);

    private JLabel selectedDayLabel;
    private JTable timetableTable;
    private DefaultTableModel tableModel;

    private List<TimetableEntry> currentEntries = new ArrayList<>();
    private String currentDay = "Monday";

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
        timetableTable.setRowHeight(34);
        timetableTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        timetableTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        timetableTable.getTableHeader().setReorderingAllowed(false);
        timetableTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Column widths
        timetableTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        timetableTable.getColumnModel().getColumn(1).setPreferredWidth(220);
        timetableTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        timetableTable.getColumnModel().getColumn(3).setPreferredWidth(150);

        timetableTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = timetableTable.getSelectedRow();
                    if (selectedRow >= 0 && selectedRow < currentEntries.size()) {
                        TimetableEntry entry = currentEntries.get(selectedRow);
                        Window parentWindow = SwingUtilities.getWindowAncestor(TimetableTablePanel.this);
                        TimetableDetailsDialog dialog = new TimetableDetailsDialog(parentWindow, entry);
                        dialog.setVisible(true);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(timetableTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        JLabel hintLabel = new JLabel("Double-click a row to view full details");
        hintLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        hintLabel.setForeground(new Color(110, 110, 110));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(cardColor);
        topPanel.add(selectedDayLabel);
        topPanel.add(Box.createVerticalStrut(5));
        topPanel.add(hintLabel);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setDaySchedule(String day, List<TimetableEntry> entries) {
        this.currentDay = day;
        this.currentEntries = new ArrayList<>();

        selectedDayLabel.setText(day + " Schedule");
        tableModel.setRowCount(0);

        if (entries == null || entries.isEmpty()) {
            tableModel.addRow(new Object[]{"-", "No lectures scheduled", "-", "-"});
            return;
        }

        currentEntries.addAll(entries);

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