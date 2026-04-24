package com.fot.system.view.dashboard.to;

import com.fot.system.config.AppTheme;
import com.fot.system.controller.MedicalApprovalController;
import com.fot.system.model.MedicalApprovalRow;
import com.fot.system.model.MedicalSessionDetail;
import com.fot.system.model.User;
import com.fot.system.view.components.CustomButton;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
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
                new Object[]{"ID", "Reg No", "Student", "Sessions", "Submitted Date", "Document"}, 0
        ) {
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        approvedTableModel = new DefaultTableModel(
                new Object[]{"ID", "Reg No", "Student", "Sessions", "Submitted Date", "Approved At", "Document"}, 0
        ) {
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
            if (!e.getValueIsAdjusting())
                showMedicalDetails(pendingTable, pendingRows, lblPendingDetailsMeta, pendingDetailsTableModel, pendingDetailsPanel);
        });

        approvedTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                showMedicalDetails(approvedTable, approvedRows, lblApprovedDetailsMeta, approvedDetailsTableModel, approvedDetailsPanel);
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
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(AppTheme.TEXT_DARK);

        JLabel subtitle = new JLabel("Review pending medical submissions and approved medical records.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(AppTheme.TEXT_SUBTLE);

        titleBlock.add(title, BorderLayout.NORTH);
        titleBlock.add(subtitle, BorderLayout.SOUTH);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        CustomButton refreshButton = createActionButton("Refresh", FontAwesomeSolid.SYNC_ALT,
                AppTheme.BTN_EDIT_BG, AppTheme.BTN_EDIT_FG, AppTheme.BTN_EDIT_HOVER);
        refreshButton.addActionListener(e -> loadMedicalData());

        CustomButton approveButton = createActionButton("Approve Selected", FontAwesomeSolid.CHECK,
                AppTheme.BTN_SAVE_BG, AppTheme.BTN_SAVE_FG, AppTheme.BTN_SAVE_HOVER);
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
        content.add(pendingDetailsPanel);

        content.add(Box.createVerticalStrut(22));
        content.add(createSectionLabel("Approved Medicals"));
        content.add(Box.createVerticalStrut(10));
        content.add(createScrollPane(approvedTable, 280));
        content.add(approvedDetailsPanel);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(AppTheme.TEXT_DARK);
        return label;
    }

    private CustomButton createActionButton(String text, FontAwesomeSolid icon, Color bg, Color fg, Color hover) {
        CustomButton b = new CustomButton(text, bg, fg, hover, new Dimension(170, 40));
        b.setIcon(FontIcon.of(icon, 14, fg));
        return b;
    }

    private DefaultTableModel createDetailsTableModel() {
        return new DefaultTableModel(new Object[]{"Course Code", "Course Name", "Type", "Session No", "Session Date"}, 0);
    }

    private JLabel createDetailsMetaLabel() {
        JLabel l = new JLabel("Select a medical row to view its linked sessions.");
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(AppTheme.TEXT_SUBTLE);
        return l;
    }

    private JPanel createDetailsPanel(JLabel meta, DefaultTableModel model) {
        JTable t = new JTable(model);
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(AppTheme.CARD_BG);
        p.add(meta, BorderLayout.NORTH);
        p.add(new JScrollPane(t), BorderLayout.CENTER);
        p.setVisible(false);
        return p;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setRowHeight(28);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return t;
    }

    private JScrollPane createScrollPane(JTable table, int h) {
        JScrollPane s = new JScrollPane(table);
        s.setPreferredSize(new Dimension(0, h));
        return s;
    }

    private void configureDocumentColumn(JTable table, int col) {}
    private void hideIdColumn(JTable table) {}

    private void loadMedicalData() {
        SwingWorker<MedicalPanelData, Void> w = new SwingWorker<>() {
            protected MedicalPanelData doInBackground() {
                return new MedicalPanelData(
                        medicalApprovalController.loadPendingMedicals(),
                        medicalApprovalController.loadApprovedMedicals()
                );
            }

            protected void done() {
                try {
                    MedicalPanelData d = get();
                    pendingRows = d.pendingRows;
                    approvedRows = d.approvedRows;
                    render(pendingTableModel, pendingRows, false);
                    render(approvedTableModel, approvedRows, true);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(TOMedicalPanel.this,
                            "Failed to load medical records.");
                }
            }
        };
        w.execute();
    }

    private void render(DefaultTableModel m, List<MedicalApprovalRow> rows, boolean approved) {
        m.setRowCount(0);
        for (MedicalApprovalRow r : rows) {
            if (approved)
                m.addRow(new Object[]{r.getMedicalId(), r.getRegistrationNo(), r.getStudentName(), r.getSessionCount(), r.getSubmittedDate(), r.getApprovedAt(), r.getMedicalDocument()});
            else
                m.addRow(new Object[]{r.getMedicalId(), r.getRegistrationNo(), r.getStudentName(), r.getSessionCount(), r.getSubmittedDate(), r.getMedicalDocument()});
        }
    }

    private void showMedicalDetails(
            JTable t,
            List<MedicalApprovalRow> rows,
            JLabel meta,
            DefaultTableModel model,
            JPanel panel
    ) {
        int row = t.getSelectedRow();
        if (row < 0) return;

        int id = Integer.parseInt(t.getValueAt(row, 0).toString());

        MedicalApprovalRow selected = null;
        for (MedicalApprovalRow r : rows) {
            if (r.getMedicalId() == id) {
                selected = r;
                break;
            }
        }

        if (selected == null) return;

        meta.setText(selected.getRegistrationNo() + " | " + selected.getStudentName());

        model.setRowCount(0);
        for (MedicalSessionDetail d : selected.getSessionDetails()) {
            model.addRow(new Object[]{d.getCourseCode(), d.getCourseName(), d.getSessionType(), d.getSessionNo(), d.getSessionDate()});
        }

        panel.setVisible(true);
    }

    private void approveSelectedMedical() {
        int row = pendingTable.getSelectedRow();
        if (row < 0) return;

        int id = Integer.parseInt(pendingTableModel.getValueAt(row, 0).toString());
        medicalApprovalController.approveMedical(id, currentUser.getId());
        loadMedicalData();
    }

    private static class MedicalPanelData {
        List<MedicalApprovalRow> pendingRows;
        List<MedicalApprovalRow> approvedRows;

        MedicalPanelData(List<MedicalApprovalRow> p, List<MedicalApprovalRow> a) {
            pendingRows = p;
            approvedRows = a;
        }
    }
}