package com.fot.system.view.dashboard.student;

import com.fot.system.model.TimetableEntry;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TimetableDetailsDialog extends JDialog {

    public TimetableDetailsDialog(Window parent, TimetableEntry entry) {
        super(parent, "Timetable Details", ModalityType.APPLICATION_MODAL);

        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addRow(contentPanel, gbc, row++, "Day", entry.getDay());
        addRow(contentPanel, gbc, row++, "Time", entry.getTimeRange());
        addRow(contentPanel, gbc, row++, "Course", entry.getCourseName());
        addRow(contentPanel, gbc, row++, "Venue", entry.getVenue());
        addRow(contentPanel, gbc, row++, "Lecturer", entry.getLecturer());

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
        buttonPanel.add(closeButton);

        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setSize(420, 300);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        JLabel labelComp = new JLabel(label + ":");
        labelComp.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(labelComp, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JLabel valueComp = new JLabel("<html><div style='width:220px;'>" + value + "</div></html>");
        valueComp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(valueComp, gbc);
    }
}