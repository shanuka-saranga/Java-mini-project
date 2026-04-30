package com.fot.system.view.dashboard.lecturer.marksGrades;

import com.fot.system.config.AppTheme;
import com.fot.system.model.dto.*;
import com.fot.system.view.shared_components.CustomButton;
import com.fot.system.view.shared_components.SectionCard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

/**
 * displays and edits student marks for a selected lecturer assessment item
 * @author janith
 */
public class AssessmentMarksDetailPanel extends JPanel {
    private final JLabel lblTitle;
    private final JTextField txtSearch;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final Runnable onSave;
    private String currentAssessmentType;
    private List<AssessmentStudentMarkRow> currentRows = List.of();

    /**
     * Creates the assessment detail panel used to view and edit student marks.
     * @param onBack action used to return to the summary view
     * @param onSave action used to save edited marks
     * @author janith
     */
    public AssessmentMarksDetailPanel(Runnable onBack, Runnable onSave) {
        setLayout(new BorderLayout(0, 16));
        setOpaque(false);
        this.onSave = onSave;

        JPanel topBar = new JPanel(new BorderLayout(12, 0));
        topBar.setOpaque(false);

        JPanel topActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        topActions.setOpaque(false);

        CustomButton backButton = new CustomButton(
                "Back to Summary",
                AppTheme.BTN_EDIT_BG,
                AppTheme.BTN_EDIT_FG,
                AppTheme.BTN_EDIT_HOVER,
                new Dimension(150, 38)
        );
        backButton.addActionListener(e -> onBack.run());
        topActions.add(backButton);

        CustomButton saveButton = new CustomButton(
                "Save",
                AppTheme.BTN_SAVE_BG,
                AppTheme.BTN_SAVE_FG,
                AppTheme.BTN_SAVE_HOVER,
                new Dimension(100, 38)
        );
        saveButton.addActionListener(e -> this.onSave.run());
        topActions.add(saveButton);

        topBar.add(topActions, BorderLayout.WEST);

        lblTitle = new JLabel("-");
        lblTitle.setFont(AppTheme.fontBold(22));
        lblTitle.setForeground(AppTheme.TEXT_DARK);
        topBar.add(lblTitle, BorderLayout.CENTER);

        add(topBar, BorderLayout.NORTH);

        JPanel controls = new JPanel(new BorderLayout(12, 12));
        controls.setOpaque(false);

        txtSearch = new JTextField();
        txtSearch.setFont(AppTheme.fontPlain(14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_MUTED, 1, false),
                new EmptyBorder(8, 10, 8, 10)
        ));
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applySearchFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applySearchFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applySearchFilter();
            }
        });
        controls.add(txtSearch, BorderLayout.CENTER);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonRow.setOpaque(false);

        CustomButton sortRegButton = new CustomButton(
                "Sort Reg",
                AppTheme.BTN_EDIT_BG,
                AppTheme.BTN_EDIT_FG,
                AppTheme.BTN_EDIT_HOVER,
                new Dimension(110, 38)
        );

        CustomButton sortMarkHighButton = new CustomButton(
                "Mark High-Low",
                AppTheme.BTN_EDIT_BG,
                AppTheme.BTN_EDIT_FG,
                AppTheme.BTN_EDIT_HOVER,
                new Dimension(130, 38)
        );

        CustomButton sortMarkLowButton = new CustomButton(
                "Mark Low-High",
                AppTheme.BTN_EDIT_BG,
                AppTheme.BTN_EDIT_FG,
                AppTheme.BTN_EDIT_HOVER,
                new Dimension(130, 38)
        );

        buttonRow.add(sortRegButton);
        buttonRow.add(sortMarkHighButton);
        buttonRow.add(sortMarkLowButton);
        controls.add(buttonRow, BorderLayout.EAST);

        tableModel = new DefaultTableModel(
                new String[]{"Registration No", "Attempt", "Exam Type", "Status", "Mark"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 3 || column == 4) {
                    return true;
                }
                return column == 2 && ("MID".equalsIgnoreCase(currentAssessmentType) || "END".equalsIgnoreCase(currentAssessmentType));
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        AppTheme.applyTableHeaderTheme(table);
        table.setFont(AppTheme.fontPlain(13));
        table.setSelectionBackground(AppTheme.TABLE_SELECTION_BG);
        table.setSelectionForeground(AppTheme.TABLE_SELECTION_FG);
        table.setGridColor(AppTheme.BORDER_LIGHT);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                setText(value == null ? "" : value.toString());
            }
        });

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        sortRegButton.addActionListener(e -> sorter.setSortKeys(List.of(new RowSorter.SortKey(0, SortOrder.ASCENDING))));
        sortMarkHighButton.addActionListener(e -> sorter.setSortKeys(List.of(new RowSorter.SortKey(4, SortOrder.DESCENDING))));
        sortMarkLowButton.addActionListener(e -> sorter.setSortKeys(List.of(new RowSorter.SortKey(4, SortOrder.ASCENDING))));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(AppTheme.CARD_BG);

        SectionCard sectionCard = new SectionCard(
                "Student Marks",
                "Only attempted, absent, and medical records are shown for the selected assessment item."
        );
        sectionCard.setContent(scrollPane);

        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setOpaque(false);
        body.add(controls, BorderLayout.NORTH);
        body.add(sectionCard, BorderLayout.CENTER);

        add(body, BorderLayout.CENTER);
    }

    /**
     * Updates the visible title for the selected assessment item.
     * @param title assessment title text
     * @author janith
     */
    public void setAssessmentTitle(String title) {
        lblTitle.setText(title);
    }

    /**
     * Configures editors and state for the selected assessment type.
     * @param assessmentType selected assessment type
     * @author janith
     */
    public void setAssessmentType(String assessmentType) {
        currentAssessmentType = assessmentType;
        table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JComboBox<>(buildStatusOptions(assessmentType))));
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JComboBox<>(buildExamTypeOptions(assessmentType))));
    }

    /**
     * Loads assessment mark rows into the editable table.
     * @param rows assessment rows to display
     * @author janith
     */
    public void setRows(List<AssessmentStudentMarkRow> rows) {
        currentRows = rows == null ? List.of() : new java.util.ArrayList<>(rows);
        tableModel.setRowCount(0);
        txtSearch.setText("");
        sorter.setSortKeys(null);

        if (rows == null || rows.isEmpty()) {
            tableModel.addRow(new Object[]{"No students found", "", "", "", ""});
            return;
        }

        for (AssessmentStudentMarkRow row : rows) {
            tableModel.addRow(new Object[]{
                    row.getRegistrationNo(),
                    String.valueOf(row.getAttemptNo()),
                    valueOrDash(row.getExamType()),
                    valueOrEmpty(row.getStatus()),
                    formatMark(row.getMark())
            });
        }
    }

    /**
     * Collects the edited student mark rows from the table model.
     * @author janith
     */
    public List<AssessmentStudentMarkRow> getEditedRows() {
        java.util.List<AssessmentStudentMarkRow> editedRows = new java.util.ArrayList<>();
        for (int rowIndex = 0; rowIndex < currentRows.size(); rowIndex++) {
            AssessmentStudentMarkRow source = currentRows.get(rowIndex);
            AssessmentStudentMarkRow row = new AssessmentStudentMarkRow();
            row.setMarkId(source.getMarkId());
            row.setRegistrationNo(source.getRegistrationNo());
            row.setAttemptNo(source.getAttemptNo());
            row.setExamType(normalizeExamType(tableModel.getValueAt(rowIndex, 2), source.getExamType()));
            row.setStatus(normalizeValue(tableModel.getValueAt(rowIndex, 3)));
            row.setMark(parseMark(tableModel.getValueAt(rowIndex, 4)));
            editedRows.add(row);
        }
        return editedRows;
    }

    /**
     * Applies the registration and status search filter to the marks table.
     * @author janith
     */
    private void applySearchFilter() {
        String query = txtSearch.getText().trim();
        if (query.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(query), 0, 2, 3));
        }
    }

    /**
     * Builds the allowed status options for the selected assessment type.
     * @param assessmentType selected assessment type
     * @author janith
     */
    private String[] buildStatusOptions(String assessmentType) {
        if ("ASSIGNMENT".equalsIgnoreCase(assessmentType)) {
            return new String[]{"", "PENDING", "SUBMITTED", "NOT_SUBMITTED", "MEDICAL"};
        }
        return new String[]{"", "PENDING", "PRESENT", "ABSENT", "MEDICAL"};
    }

    /**
     * Builds the available exam type values for exam-based assessments.
     * @param assessmentType selected assessment type
     * @author janith
     */
    private String[] buildExamTypeOptions(String assessmentType) {
        if ("MID".equalsIgnoreCase(assessmentType) || "END".equalsIgnoreCase(assessmentType)) {
            return new String[]{"THEORY", "PRACTICAL"};
        }
        return new String[]{"-"};
    }

    /**
     * Formats a mark value for display in the table.
     * @param mark numeric mark value
     * @author janith
     */
    private String formatMark(Double mark) {
        if (mark == null) {
            return "";
        }
        return String.format("%.2f", mark);
    }

    /**
     * Returns a dash when the given value is blank.
     * @param value source text value
     * @author janith
     */
    private String valueOrDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    /**
     * Returns an empty string when the given value is blank.
     * @param value source text value
     * @author janith
     */
    private String valueOrEmpty(String value) {
        return value == null || value.isBlank() ? "" : value;
    }

    /**
     * Normalizes a table cell value into trimmed text.
     * @param value raw table value
     * @author janith
     */
    private String normalizeValue(Object value) {
        return value == null ? "" : value.toString().trim();
    }

    /**
     * Parses the entered mark text into a numeric value.
     * @param value raw table value
     * @author janith
     */
    private Double parseMark(Object value) {
        String text = normalizeValue(value);
        if (text.isEmpty()) {
            return null;
        }
        return Double.parseDouble(text);
    }

    /**
     * Resolves the selected exam type while preserving the original fallback.
     * @param value selected table value
     * @param fallback original exam type value
     * @author janith
     */
    private String normalizeExamType(Object value, String fallback) {
        String text = normalizeValue(value);
        if (text.isEmpty() || "-".equals(text)) {
            return fallback;
        }
        return text;
    }
}
