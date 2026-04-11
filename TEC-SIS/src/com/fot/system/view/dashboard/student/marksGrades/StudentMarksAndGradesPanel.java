package com.fot.system.view.dashboard.student.marksGrades;

import com.fot.system.config.AppTheme;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.service.StudentMarksGradesService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public class StudentMarksAndGradesPanel extends JPanel {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    private final User currentUser;
    private final StudentMarksGradesService studentMarksGradesService;
    private final DefaultTableModel tableModel;
    private final JTable marksTable;
    private final JLabel lblCurrentSemester;
    private final JLabel lblCurrentSgpa;
    private final JLabel lblCurrentCgpa;

    public StudentMarksAndGradesPanel(User user) {
        this.currentUser = user;
        this.studentMarksGradesService = new StudentMarksGradesService();

        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.SURFACE_SOFT);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        add(createHeader(), BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[]{"Course Code", "Course Name", "Semester", "Credits", "CA %", "End %", "Final Mark", "Grade"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        marksTable = new JTable(tableModel);
        marksTable.setRowHeight(30);
        marksTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        marksTable.setForeground(AppTheme.TEXT_DARK);
        marksTable.setGridColor(AppTheme.BORDER_SOFT);
        marksTable.setSelectionBackground(AppTheme.TABLE_SELECTION_BG);
        marksTable.setSelectionForeground(AppTheme.TABLE_SELECTION_FG);
        marksTable.getTableHeader().setBackground(AppTheme.TABLE_HEADER_BG);
        marksTable.getTableHeader().setForeground(AppTheme.TABLE_HEADER_FG);
        marksTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        marksTable.setFillsViewportHeight(true);
        marksTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane tableScrollPane = new JScrollPane(marksTable);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, true));
        tableScrollPane.getViewport().setBackground(AppTheme.CARD_BG);
        tableScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        lblCurrentSemester = createMetaLabel("Current Semester Year: -");
        lblCurrentSgpa = createMetaLabel("Current SGPA: 0.00");
        lblCurrentCgpa = createMetaLabel("Current CGPA: 0.00");

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.add(lblCurrentSemester);
        bottomPanel.add(lblCurrentSgpa);
        bottomPanel.add(lblCurrentCgpa);

        JPanel content = new JPanel(new BorderLayout(0, 14));
        content.setOpaque(false);
        content.add(tableScrollPane, BorderLayout.CENTER);
        content.add(bottomPanel, BorderLayout.SOUTH);

        add(content, BorderLayout.CENTER);
        loadMarksAndGrades();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 8));
        header.setOpaque(false);

        JLabel title = new JLabel("Marks / Grades");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(AppTheme.TEXT_DARK);

        JLabel subtitle = new JLabel("Review your subject marks, final grades, current SGPA, and current CGPA.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(AppTheme.TEXT_SUBTLE);

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        return header;
    }

    private JLabel createMetaLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(AppTheme.TEXT_DARK);
        return label;
    }

    private void loadMarksAndGrades() {
        SwingWorker<StudentMarksGradeViewData, Void> worker = new SwingWorker<StudentMarksGradeViewData, Void>() {
            @Override
            protected StudentMarksGradeViewData doInBackground() {
                return studentMarksGradesService.getStudentMarksGradeViewData(currentUser.getId());
            }

            @Override
            protected void done() {
                try {
                    StudentMarksGradeViewData viewData = get();
                    renderRows(viewData.getRows());
                    lblCurrentSemester.setText("Current Semester Year: " + valueOrDash(viewData.getCurrentSemesterYear()));
                    lblCurrentSgpa.setText("Current SGPA: " + DECIMAL_FORMAT.format(viewData.getCurrentSgpa()));
                    lblCurrentCgpa.setText("Current CGPA: " + DECIMAL_FORMAT.format(viewData.getCurrentCgpa()));
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            StudentMarksAndGradesPanel.this,
                            "Failed to load marks and grades.",
                            "Marks Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void renderRows(List<StudentSubjectGradeRow> rows) {
        tableModel.setRowCount(0);

        if (rows == null || rows.isEmpty()) {
            tableModel.addRow(new Object[]{"-", "No marks available.", "-", "-", "-", "-", "-", "-"});
            return;
        }

        for (StudentSubjectGradeRow row : rows) {
            tableModel.addRow(new Object[]{
                    row.getCourseCode(),
                    row.getCourseName(),
                    row.getSemesterYear(),
                    row.getCredits(),
                    DECIMAL_FORMAT.format(row.getCaAverage()),
                    DECIMAL_FORMAT.format(row.getEndExamAverage()),
                    row.getFinalMark() == null ? "-" : DECIMAL_FORMAT.format(row.getFinalMark()),
                    row.getGrade()
            });
        }
    }

    private String valueOrDash(int value) {
        return value <= 0 ? "-" : String.valueOf(value);
    }
}
