package com.fot.system.view.dashboard.lecturer.marksGrades;

import com.fot.system.config.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AssessmentSummaryCard extends JPanel {

    private final JLabel lblTitle;
    private final JLabel lblAverage;
    private final JLabel lblAttempts;
    private final JLabel lblAbsent;
    private final JLabel lblMedical;

    public AssessmentSummaryCard() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(AppTheme.CARD_BG);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.CARD_BORDER, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));

        lblTitle = createLabel("-", Font.BOLD, 18, AppTheme.TEXT_DARK);
        lblAverage = createLabel("Avg: 0.00", Font.BOLD, 24, AppTheme.TEXT_DARK);
        lblAttempts = createLabel("Attempts: 0", Font.PLAIN, 13, AppTheme.TEXT_SUBTLE);
        lblAbsent = createLabel("Absent: 0", Font.PLAIN, 13, AppTheme.TEXT_SUBTLE);
        lblMedical = createLabel("Medical: 0", Font.PLAIN, 13, AppTheme.TEXT_SUBTLE);

        add(lblTitle);
        add(Box.createVerticalStrut(14));
        add(lblAverage);
        add(Box.createVerticalStrut(10));
        add(lblAttempts);
        add(Box.createVerticalStrut(4));
        add(lblAbsent);
        add(Box.createVerticalStrut(4));
        add(lblMedical);
        add(Box.createVerticalStrut(4));
        add(Box.createVerticalGlue());

        setPreferredSize(new Dimension(260, 164));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 164));
    }

    public void setSummary(String title, String average, int attempts, int absent, int medical) {
        lblTitle.setText(title);
        lblAverage.setText("Avg: " + average);
        lblAttempts.setText("Attempts: " + attempts);
        lblAbsent.setText("Absent: " + absent);
        lblMedical.setText("Medical: " + medical);
    }

    private JLabel createLabel(String text, int style, int size, Color color) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setFont(new Font("Segoe UI", style, size));
        label.setForeground(color);
        return label;
    }
}
