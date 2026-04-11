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

public class AddCourseMaterialDialog extends JDialog {

    private final JTextField txtTitle;
    private final JTextArea txtDescription;
    private final JTextField txtFilePath;
    private final JTextField txtFileType;
    private AddCourseMaterialRequest request;

    public AddCourseMaterialDialog(Window owner, Course course, int uploadedBy) {
        super(owner, "Add New Material", ModalityType.APPLICATION_MODAL);
        setSize(520, 420);
        setLocationRelativeTo(owner);

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(AppTheme.SURFACE_SOFT);
        root.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Add Material for " + course.getCourseCode());
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(AppTheme.TEXT_DARK);

        JPanel form = new JPanel();
        form.setBackground(AppTheme.CARD_BG);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, true),
                new EmptyBorder(18, 18, 18, 18)
        ));
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        txtTitle = new JTextField();
        styleTextField(txtTitle);

        txtDescription = new JTextArea(4, 20);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDescription.setBorder(new EmptyBorder(10, 10, 10, 10));

        txtFilePath = new JTextField();
        txtFilePath.setEditable(false);
        styleTextField(txtFilePath);

        txtFileType = new JTextField();
        styleTextField(txtFileType);

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

        CustomButton save = new CustomButton("Save Material", AppTheme.BTN_SAVE_BG, AppTheme.BTN_SAVE_FG, AppTheme.BTN_SAVE_HOVER, new Dimension(140, 40));
        save.addActionListener(e -> {
            request = new AddCourseMaterialRequest(
                    String.valueOf(course.getId()),
                    course.getCourseCode(),
                    txtTitle.getText().trim(),
                    txtDescription.getText().trim(),
                    txtFilePath.getText().trim(),
                    txtFileType.getText().trim(),
                    uploadedBy
            );
            dispose();
        });

        actions.add(cancel);
        actions.add(save);

        root.add(title, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);
        setContentPane(root);
    }

    public AddCourseMaterialRequest getRequest() {
        return request;
    }

    private JPanel createField(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
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
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_MUTED),
                new EmptyBorder(10, 12, 10, 12)
        ));
    }
}
