package com.fot.system.view.dashboard.lecturer.studentDetails;

import com.fot.system.config.AppTheme;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.service.LecturerStudentDetailsService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class StudentDetailsPanel extends JPanel {
    private static final DecimalFormat DF = new DecimalFormat("0.00");

    private final LecturerStudentDetailsService studentDetailsService = new LecturerStudentDetailsService();
    private final DefaultTableModel tableModel;
    private final JTable studentTable;
    private final TableRowSorter<DefaultTableModel> rowSorter;
    private final JTextField txtSearch;
    private final JComboBox<String> cmbBatch;

    public StudentDetailsPanel(User user) {
        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.SURFACE_SOFT);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        add(createHeader(), BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 16));
        contentPanel.setOpaque(false);

        JPanel controlsPanel = new JPanel(new BorderLayout(12, 0));
        controlsPanel.setOpaque(false);

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(400, 40));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, true),
                new EmptyBorder(0, 10, 0, 10)
        ));
        txtSearch.setToolTipText("Search by Name, Reg No or Email...");
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilters(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilters(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilters(); }
        });

        cmbBatch = new JComboBox<>();
        cmbBatch.setPreferredSize(new Dimension(150, 40));
        cmbBatch.addActionListener(e -> applyFilters());

        controlsPanel.add(txtSearch, BorderLayout.CENTER);
        controlsPanel.add(cmbBatch, BorderLayout.EAST);

        tableModel = new DefaultTableModel(
                new Object[]{
                        "Reg No", "Name", "Year", "Type", "Email", "Phone", "Address", "SGPA", "CGPA"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        studentTable = new JTable(tableModel);
        styleTable(studentTable);

        rowSorter = new TableRowSorter<>(tableModel);
        studentTable.setRowSorter(rowSorter);

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, true));
        scrollPane.getViewport().setBackground(AppTheme.CARD_BG);

        contentPanel.add(controlsPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);
        loadData();
    }

    private void loadData() {
        List<StudentDetailsRow> data = studentDetailsService.getLectureViewStudentDetails();
        tableModel.setRowCount(0);

        Set<String> batches = new TreeSet<>();
        batches.add("All Batches");

        for (StudentDetailsRow row : data) {
            tableModel.addRow(new Object[]{
                    row.getRegNo(),
                    row.getFullName(),
                    row.getRegistrationYear(),
                    row.getStudentType(),
                    row.getEmail(),
                    row.getPhone(),
                    row.getAddress(),
                    DF.format(row.getSgpa()),
                    DF.format(row.getCgpa())
            });

            batches.add(String.valueOf(row.getRegistrationYear()));
        }

        cmbBatch.removeAllItems();
        for (String b : batches) cmbBatch.addItem(b);
    }

    private void applyFilters() {
        String search = txtSearch.getText().toLowerCase();
        String batch = cmbBatch.getSelectedItem() != null ? cmbBatch.getSelectedItem().toString() : "All Batches";

        rowSorter.setRowFilter(new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ?> entry) {
                String reg = entry.getStringValue(0).toLowerCase();
                String name = entry.getStringValue(1).toLowerCase();
                String email = entry.getStringValue(4).toLowerCase();
                String bValue = entry.getStringValue(2);

                boolean matchesSearch = reg.contains(search) || name.contains(search) || email.contains(search);
                boolean matchesBatch = batch.equals("All Batches") || bValue.equals(batch);

                return matchesSearch && matchesBatch;
            }
        });
    }

    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setGridColor(AppTheme.BORDER_SOFT);
        table.setSelectionBackground(AppTheme.TABLE_SELECTION_BG);
        table.setSelectionForeground(AppTheme.TABLE_SELECTION_FG);
        table.setShowVerticalLines(false);

        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
        table.getColumnModel().getColumn(4).setPreferredWidth(180); // Email
        table.getColumnModel().getColumn(6).setPreferredWidth(150); // Address

        table.getTableHeader().setBackground(AppTheme.TABLE_HEADER_BG);
        table.getTableHeader().setForeground(AppTheme.TABLE_HEADER_FG);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
    }

    private JPanel createHeader() {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        JLabel title = new JLabel("Student Overall Performance");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(AppTheme.TEXT_DARK);

        JLabel subtitle = new JLabel("Comprehensive student profiles with contact information and academic GPA.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(AppTheme.TEXT_SUBTLE);

        p.add(title, BorderLayout.NORTH);
        p.add(subtitle, BorderLayout.SOUTH);
        return p;
    }
}