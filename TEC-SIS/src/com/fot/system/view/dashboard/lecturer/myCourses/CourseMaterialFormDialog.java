package com.fot.system.view.dashboard.lecturer.myCourses;

import com.fot.system.config.AppTheme;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.view.components.CustomButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class CourseMaterialFormDialog extends JDialog {

    private final JTextField txtTitle;
    private final JTextArea txtDescription;
    private final JTextField txtFilePath;
    private final JTextField txtFileType;
    private boolean confirmed;

    public CourseMaterialFormDialog(Window owner, String dialogTitle, Course course, CourseMaterial material) {
        super(owner, dialogTitle, ModalityType.APPLICATION_MODAL);
        setSize(520, 420);
        setLocationRelativeTo(owner);

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(AppTheme.SURFACE_SOFT);
        root.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel(dialogTitle + " - " + course.getCourseCode());
        title.setFont(AppTheme.fontBold(22));
        title.setForeground(AppTheme.TEXT_DARK);

        JPanel form = new JPanel();
        form.setBackground(AppTheme.CARD_BG);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, false),
                new EmptyBorder(18, 18, 18, 18)
        ));
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        txtTitle = new JTextField();
        styleTextField(txtTitle);

        txtDescription = new JTextArea(4, 20);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setFont(AppTheme.fontPlain(14));
        txtDescription.setBorder(new EmptyBorder(10, 10, 10, 10));

        txtFilePath = new JTextField();
        txtFilePath.setEditable(false);
        styleTextField(txtFilePath);

        txtFileType = new JTextField();
        styleTextField(txtFileType);

        if (material != null) {
            txtTitle.setText(valueOrEmpty(material.getTitle()));
            txtDescription.setText(valueOrEmpty(material.getDescription()));
            txtFilePath.setText(valueOrEmpty(material.getFilePath()));
            txtFileType.setText(valueOrEmpty(material.getFileType()));
        }

        form.add(createField("Title", txtTitle));
        form.add(Box.createVerticalStrut(12));
        form.add(createField("Description", createTextAreaPane()));
        form.add(Box.createVerticalStrut(12));
        form.add(createField("File", createFilePickerRow()));
        form.add(Box.createVerticalStrut(12));
        form.add(createField("File Type", txtFileType));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        CustomButton cancel = new CustomButton("Cancel", AppTheme.BTN_CANCEL_BG, AppTheme.BTN_CANCEL_FG, AppTheme.BTN_CANCEL_HOVER, new Dimension(110, 40));
        cancel.addActionListener(e -> dispose());

        CustomButton save = new CustomButton(
                material == null ? "Save Material" : "Update Material",
                AppTheme.BTN_SAVE_BG,
                AppTheme.BTN_SAVE_FG,
                AppTheme.BTN_SAVE_HOVER,
                new Dimension(150, 40)
        );
        save.addActionListener(e -> handleSave());

        actions.add(cancel);
        actions.add(save);

        root.add(title, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);
        setContentPane(root);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getTitleValue() {
        return txtTitle.getText().trim();
    }

    public String getDescriptionValue() {
        return txtDescription.getText().trim();
    }

    public String getFilePathValue() {
        return txtFilePath.getText().trim();
    }

    public String getFileTypeValue() {
        return txtFileType.getText().trim();
    }

    private void handleSave() {
        if (getTitleValue().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Material title is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (getFilePathValue().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a material file.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        confirmed = true;
        dispose();
    }

    private JPanel createField(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(AppTheme.fontPlain(13));
        label.setForeground(AppTheme.TEXT_DARK);

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFilePickerRow() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);

        CustomButton browseButton = new CustomButton(
                "Browse",
                AppTheme.BTN_EDIT_BG,
                AppTheme.BTN_EDIT_FG,
                AppTheme.BTN_EDIT_HOVER,
                new Dimension(110, 40)
        );
        browseButton.addActionListener(e -> chooseFile());

        panel.add(txtFilePath, BorderLayout.CENTER);
        panel.add(browseButton, BorderLayout.EAST);
        return panel;
    }

    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Material File");
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "Supported Files",
                "pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "txt", "zip", "rar", "jpg", "jpeg", "png"
        ));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            txtFilePath.setText(selectedFile.getAbsolutePath());
            String fileName = selectedFile.getName();
            int lastDotIndex = fileName.lastIndexOf('.');
            txtFileType.setText(lastDotIndex >= 0 ? fileName.substring(lastDotIndex + 1).toUpperCase() : "");
        }
    }

    private JScrollPane createTextAreaPane() {
        JScrollPane scrollPane = new JScrollPane(txtDescription);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_MUTED),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));
        scrollPane.getViewport().setBackground(AppTheme.CARD_BG);
        return scrollPane;
    }

    private void styleTextField(JTextField textField) {
        textField.setFont(AppTheme.fontPlain(14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_MUTED),
                new EmptyBorder(10, 12, 10, 12)
        ));
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
