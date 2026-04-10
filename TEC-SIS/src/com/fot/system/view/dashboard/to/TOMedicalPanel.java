package com.fot.system.view.dashboard.to;

import com.fot.system.config.AppTheme;
import com.fot.system.controller.MedicalApprovalController;
import com.fot.system.model.MedicalApprovalRow;
import com.fot.system.model.User;
import com.fot.system.view.components.CustomButton;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.io.File;
import java.util.List;

public class TOMedicalPanel extends JPanel {

    private final User currentUser;
    private final MedicalApprovalController medicalApprovalController;
    private final DefaultTableModel pendingTableModel;
    private final DefaultTableModel approvedTableModel;
    private final JTable pendingTable;
    private final JTable approvedTable;

    public TOMedicalPanel(User user) {
        this.currentUser = user;
        this.medicalApprovalController = new MedicalApprovalController();

        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.SURFACE_SOFT);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        add(createHeader(), BorderLayout.NORTH);

        pendingTableModel = new DefaultTableModel(
                new Object[]{"ID", "Reg No", "Student", "Course Code", "Course Name", "Type", "Session No", "Session Date", "Submitted Date", "Document"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 9;
            }
        };

        approvedTableModel = new DefaultTableModel(
                new Object[]{"ID", "Reg No", "Student", "Course Code", "Course Name", "Type", "Session No", "Session Date", "Submitted Date", "Approved At", "Document"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 10;
            }
        };

        pendingTable = createStyledTable(pendingTableModel);
        approvedTable = createStyledTable(approvedTableModel);
        configureDocumentColumn(pendingTable, 9);
        configureDocumentColumn(approvedTable, 10);
        hideIdColumn(pendingTable);
        hideIdColumn(approvedTable);

        add(createContent(), BorderLayout.CENTER);
        loadMedicalData();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titleBlock = new JPanel(new BorderLayout(0, 8));
        titleBlock.setOpaque(false);

        JLabel title = new JLabel("Medicals");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(AppTheme.TEXT_DARK);

        JLabel subtitle = new JLabel("Review pending medical submissions and approved medical records.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(AppTheme.TEXT_SUBTLE);

        titleBlock.add(title, BorderLayout.NORTH);
        titleBlock.add(subtitle, BorderLayout.SOUTH);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        CustomButton refreshButton = createActionButton("Refresh", FontAwesomeSolid.SYNC_ALT, AppTheme.BTN_EDIT_BG, AppTheme.BTN_EDIT_FG, AppTheme.BTN_EDIT_HOVER);
        refreshButton.addActionListener(e -> loadMedicalData());

        CustomButton approveButton = createActionButton("Approve Selected", FontAwesomeSolid.CHECK, AppTheme.BTN_SAVE_BG, AppTheme.BTN_SAVE_FG, AppTheme.BTN_SAVE_HOVER);
        approveButton.addActionListener(e -> approveSelectedMedical());

        actions.add(refreshButton);
        actions.add(approveButton);

        header.add(titleBlock, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private JComponent createContent() {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(createSectionLabel("Pending Medical Approvals"));
        content.add(Box.createVerticalStrut(10));
        content.add(createScrollPane(pendingTable, 280));
        content.add(Box.createVerticalStrut(22));
        content.add(createSectionLabel("Approved Medicals"));
        content.add(Box.createVerticalStrut(10));
        content.add(createScrollPane(approvedTable, 280));

        JScrollPane mainScrollPane = new JScrollPane(content);
        mainScrollPane.setBorder(null);
        mainScrollPane.getViewport().setBackground(AppTheme.SURFACE_SOFT);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return mainScrollPane;
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(AppTheme.TEXT_DARK);
        return label;
    }

    private CustomButton createActionButton(String text, FontAwesomeSolid icon, Color bg, Color fg, Color hover) {
        CustomButton button = new CustomButton(text, bg, fg, hover, new Dimension(170, 40));
        button.setIcon(FontIcon.of(icon, 14, fg));
        return button;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(AppTheme.TEXT_DARK);
        table.setGridColor(AppTheme.BORDER_SOFT);
        table.setSelectionBackground(AppTheme.TABLE_SELECTION_BG);
        table.setSelectionForeground(AppTheme.TABLE_SELECTION_FG);
        table.getTableHeader().setBackground(AppTheme.TABLE_HEADER_BG);
        table.getTableHeader().setForeground(AppTheme.TABLE_HEADER_FG);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return table;
    }

    private JScrollPane createScrollPane(JTable table, int preferredHeight) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, true));
        scrollPane.getViewport().setBackground(AppTheme.CARD_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setPreferredSize(new Dimension(0, preferredHeight));
        return scrollPane;
    }

    private void configureDocumentColumn(JTable table, int columnIndex) {
        TableColumn documentColumn = table.getColumnModel().getColumn(columnIndex);
        documentColumn.setPreferredWidth(90);
        documentColumn.setMaxWidth(90);
        documentColumn.setMinWidth(90);
        documentColumn.setCellRenderer(new DocumentActionCellRenderer());
        documentColumn.setCellEditor(new DocumentActionCellEditor(table, columnIndex));
    }

    private void hideIdColumn(JTable table) {
        TableColumn idColumn = table.getColumnModel().getColumn(0);
        idColumn.setMinWidth(0);
        idColumn.setMaxWidth(0);
        idColumn.setPreferredWidth(0);
    }

    private void loadMedicalData() {
        SwingWorker<MedicalPanelData, Void> worker = new SwingWorker<>() {
            @Override
            protected MedicalPanelData doInBackground() {
                return new MedicalPanelData(
                        medicalApprovalController.loadPendingMedicals(),
                        medicalApprovalController.loadApprovedMedicals()
                );
            }

            @Override
            protected void done() {
                try {
                    MedicalPanelData data = get();
                    renderPendingRows(data.pendingRows);
                    renderApprovedRows(data.approvedRows);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            TOMedicalPanel.this,
                            "Failed to load medical records.",
                            "Medical Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void renderPendingRows(List<MedicalApprovalRow> rows) {
        pendingTableModel.setRowCount(0);
        for (MedicalApprovalRow row : rows) {
            pendingTableModel.addRow(new Object[]{
                    row.getMedicalId(),
                    row.getRegistrationNo(),
                    row.getStudentName(),
                    row.getCourseCode(),
                    row.getCourseName(),
                    row.getSessionType(),
                    row.getSessionNo(),
                    row.getSessionDate(),
                    row.getSubmittedDate(),
                    row.getMedicalDocument()
            });
        }
    }

    private void renderApprovedRows(List<MedicalApprovalRow> rows) {
        approvedTableModel.setRowCount(0);
        for (MedicalApprovalRow row : rows) {
            approvedTableModel.addRow(new Object[]{
                    row.getMedicalId(),
                    row.getRegistrationNo(),
                    row.getStudentName(),
                    row.getCourseCode(),
                    row.getCourseName(),
                    row.getSessionType(),
                    row.getSessionNo(),
                    row.getSessionDate(),
                    row.getSubmittedDate(),
                    row.getApprovedAt(),
                    row.getMedicalDocument()
            });
        }
    }

    private void approveSelectedMedical() {
        int selectedRow = pendingTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a pending medical record first.", "Medical", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = pendingTable.convertRowIndexToModel(selectedRow);
        int medicalId = Integer.parseInt(String.valueOf(pendingTableModel.getValueAt(modelRow, 0)));

        try {
            medicalApprovalController.approveMedical(medicalId, currentUser.getId());
            JOptionPane.showMessageDialog(this, "Medical approved successfully.");
            loadMedicalData();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Medical Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openDocument(String path) {
        if (path == null || path.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Document is not available.", "Medical", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Desktop.getDesktop().open(new File(path));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unable to open document.", "Medical", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class MedicalPanelData {
        private final List<MedicalApprovalRow> pendingRows;
        private final List<MedicalApprovalRow> approvedRows;

        private MedicalPanelData(List<MedicalApprovalRow> pendingRows, List<MedicalApprovalRow> approvedRows) {
            this.pendingRows = pendingRows;
            this.approvedRows = approvedRows;
        }
    }

    private static class DocumentActionCellRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JButton button = new JButton(FontIcon.of(FontAwesomeSolid.EXTERNAL_LINK_ALT, 14, AppTheme.PRIMARY));
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setOpaque(true);
            button.setBackground(isSelected ? AppTheme.TABLE_SELECTION_BG : AppTheme.CARD_BG);
            return button;
        }
    }

    private class DocumentActionCellEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton button;
        private final JTable table;
        private final int columnIndex;
        private String currentPath;

        private DocumentActionCellEditor(JTable table, int columnIndex) {
            this.table = table;
            this.columnIndex = columnIndex;
            this.button = new JButton(FontIcon.of(FontAwesomeSolid.EXTERNAL_LINK_ALT, 14, AppTheme.PRIMARY));
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.addActionListener(e -> {
                fireEditingStopped();
                openDocument(currentPath);
            });
        }

        @Override
        public Object getCellEditorValue() {
            return currentPath;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            int modelRow = this.table.convertRowIndexToModel(row);
            currentPath = String.valueOf(this.table.getModel().getValueAt(modelRow, columnIndex));
            return button;
        }
    }
}
