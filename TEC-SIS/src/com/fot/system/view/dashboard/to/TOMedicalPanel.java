package com.fot.system.view.dashboard.to;

import com.fot.system.config.AppTheme;
import com.fot.system.controller.MedicalApprovalController;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
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
    private final DefaultTableModel pendingDetailsTableModel;
    private final DefaultTableModel approvedDetailsTableModel;
    private final JLabel lblPendingDetailsMeta;
    private final JLabel lblApprovedDetailsMeta;
    private final JPanel pendingDetailsPanel;
    private final JPanel approvedDetailsPanel;

    private List<MedicalApprovalRow> pendingRows = List.of();
    private List<MedicalApprovalRow> approvedRows = List.of();

    public TOMedicalPanel(User user) {
        this.currentUser = user;
        this.medicalApprovalController = new MedicalApprovalController();

        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.SURFACE_SOFT);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        add(createHeader(), BorderLayout.NORTH);

        pendingTableModel = new DefaultTableModel(
                new Object[]{"ID", "Reg No", "Student", "Sessions", "Submitted Date", "Document"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        approvedTableModel = new DefaultTableModel(
                new Object[]{"ID", "Reg No", "Student", "Sessions", "Submitted Date", "Approved At", "Document"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };

        pendingDetailsTableModel = createDetailsTableModel();
        approvedDetailsTableModel = createDetailsTableModel();
        lblPendingDetailsMeta = createDetailsMetaLabel();
        lblApprovedDetailsMeta = createDetailsMetaLabel();

        pendingTable = createStyledTable(pendingTableModel);
        approvedTable = createStyledTable(approvedTableModel);
        configureDocumentColumn(pendingTable, 5);
        configureDocumentColumn(approvedTable, 6);
        hideIdColumn(pendingTable);
        hideIdColumn(approvedTable);

        pendingDetailsPanel = createDetailsPanel(lblPendingDetailsMeta, pendingDetailsTableModel);
        approvedDetailsPanel = createDetailsPanel(lblApprovedDetailsMeta, approvedDetailsTableModel);
        pendingTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showMedicalDetails(pendingTable, pendingRows, lblPendingDetailsMeta, pendingDetailsTableModel, pendingDetailsPanel);
            }
        });
        approvedTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showMedicalDetails(approvedTable, approvedRows, lblApprovedDetailsMeta, approvedDetailsTableModel, approvedDetailsPanel);
            }
        });

        add(createContent(), BorderLayout.CENTER);
        loadMedicalData();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titleBlock = new JPanel(new BorderLayout(0, 8));
        titleBlock.setOpaque(false);

        JLabel title = new JLabel("Medicals");
        title.setFont(AppTheme.fontBold(28));
        title.setForeground(AppTheme.TEXT_DARK);

        JLabel subtitle = new JLabel("Review pending medical submissions and approved medical records.");
        subtitle.setFont(AppTheme.fontPlain(14));
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
        content.add(Box.createVerticalStrut(10));
        content.add(pendingDetailsPanel);
        content.add(Box.createVerticalStrut(22));
        content.add(createSectionLabel("Approved Medicals"));
        content.add(Box.createVerticalStrut(10));
        content.add(createScrollPane(approvedTable, 280));
        content.add(Box.createVerticalStrut(10));
        content.add(approvedDetailsPanel);

        JScrollPane mainScrollPane = new JScrollPane(content);
        mainScrollPane.setBorder(null);
        mainScrollPane.getViewport().setBackground(AppTheme.SURFACE_SOFT);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return mainScrollPane;
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.fontBold(18));
        label.setForeground(AppTheme.TEXT_DARK);
        return label;
    }

    private CustomButton createActionButton(String text, FontAwesomeSolid icon, Color bg, Color fg, Color hover) {
        CustomButton button = new CustomButton(text, bg, fg, hover, new Dimension(170, 40));
        button.setIcon(FontIcon.of(icon, 14, fg));
        return button;
    }

    private DefaultTableModel createDetailsTableModel() {
        return new DefaultTableModel(new Object[]{"Course Code", "Course Name", "Type", "Session No", "Session Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JLabel createDetailsMetaLabel() {
        JLabel label = new JLabel("Select a medical row to view its linked sessions.");
        label.setFont(AppTheme.fontPlain(13));
        label.setForeground(AppTheme.TEXT_SUBTLE);
        return label;
    }

    private JPanel createDetailsPanel(JLabel metaLabel, DefaultTableModel detailsModel) {
        JTable detailsTable = createStyledTable(detailsModel);
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(AppTheme.CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, true),
                new EmptyBorder(14, 14, 14, 14)
        ));
        panel.add(metaLabel, BorderLayout.NORTH);
        panel.add(createScrollPane(detailsTable, 140), BorderLayout.CENTER);
        panel.setVisible(false);
        return panel;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(AppTheme.fontPlain(13));
        table.setForeground(AppTheme.TEXT_DARK);
        table.setGridColor(AppTheme.BORDER_SOFT);
        table.setSelectionBackground(AppTheme.TABLE_SELECTION_BG);
        table.setSelectionForeground(AppTheme.TABLE_SELECTION_FG);
        table.getTableHeader().setBackground(AppTheme.TABLE_HEADER_BG);
        table.getTableHeader().setForeground(AppTheme.TABLE_HEADER_FG);
        table.getTableHeader().setFont(AppTheme.fontBold(13));
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
                    pendingRows = data.pendingRows;
                    approvedRows = data.approvedRows;
                    renderPendingRows(data.pendingRows);
                    renderApprovedRows(data.approvedRows);
                    clearDetails(lblPendingDetailsMeta, pendingDetailsTableModel, pendingDetailsPanel);
                    clearDetails(lblApprovedDetailsMeta, approvedDetailsTableModel, approvedDetailsPanel);
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
                    row.getSessionCount(),
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
                    row.getSessionCount(),
                    row.getSubmittedDate(),
                    row.getApprovedAt(),
                    row.getMedicalDocument()
            });
        }
    }

    private void showMedicalDetails(
            JTable sourceTable,
            List<MedicalApprovalRow> rows,
            JLabel metaLabel,
            DefaultTableModel detailsModel,
            JPanel detailsPanel
    ) {
        int selectedRow = sourceTable.getSelectedRow();
        if (selectedRow < 0) {
            clearDetails(metaLabel, detailsModel, detailsPanel);
            return;
        }

        int modelRow = sourceTable.convertRowIndexToModel(selectedRow);
        int medicalId = Integer.parseInt(String.valueOf(sourceTable.getModel().getValueAt(modelRow, 0)));

        MedicalApprovalRow selected = rows.stream()
                .filter(row -> row.getMedicalId() == medicalId)
                .findFirst()
                .orElse(null);

        if (selected == null) {
            clearDetails(metaLabel, detailsModel, detailsPanel);
            return;
        }

        metaLabel.setText(
                selected.getRegistrationNo() + " | " +
                        selected.getStudentName() + " | Submitted: " + selected.getSubmittedDate() +
                        (selected.getApprovedAt().isEmpty() ? "" : " | Approved: " + selected.getApprovedAt())
        );

        detailsModel.setRowCount(0);
        for (MedicalSessionDetail detail : selected.getSessionDetails()) {
            detailsModel.addRow(new Object[]{
                    detail.getCourseCode(),
                    detail.getCourseName(),
                    detail.getSessionType(),
                    detail.getSessionNo(),
                    detail.getSessionDate()
            });
        }

        detailsPanel.setVisible(true);
        detailsPanel.revalidate();
        detailsPanel.repaint();
    }

    private void clearDetails(JLabel metaLabel, DefaultTableModel detailsModel, JPanel detailsPanel) {
        metaLabel.setText("Select a medical row to view its linked sessions.");
        detailsModel.setRowCount(0);
        detailsPanel.setVisible(false);
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
