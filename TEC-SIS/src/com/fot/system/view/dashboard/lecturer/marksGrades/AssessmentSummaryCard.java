package com.fot.system.view.dashboard.lecturer.marksGrades;

import com.fot.system.config.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * shows one assessment summary card with average and status counts
 * @author janith
 */
public class AssessmentSummaryCard extends JPanel {

    private final JLabel lblTitle;
    private final JLabel lblAverage;
    private final JLabel lblCounts;

    /**
     * Creates a reusable card for displaying one assessment summary.
     * @author janith
     */
    public AssessmentSummaryCard() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(AppTheme.CARD_BG);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BASE_COLOR, 1, false),
                new EmptyBorder(16, 16, 16, 16)
        ));

        lblTitle = createLabel("-", Font.BOLD, 18, AppTheme.TEXT_DARK);
        lblAverage = createLabel("Avg: 0.00", Font.BOLD, 24, AppTheme.TEXT_DARK);
        lblCounts = createLabel("Attempts: 0 | Absent: 0 | Medical: 0 | Pending: 0", Font.PLAIN, 13, AppTheme.TEXT_SUBTLE);

        add(lblTitle);
        add(Box.createVerticalStrut(14));
        add(lblAverage);
        add(Box.createVerticalGlue());
        add(lblCounts);

        setPreferredSize(new Dimension(260, 164));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 164));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Updates the summary card content with assessment statistics.
     * @param title summary title
     * @param average formatted average mark
     * @param attempts number of attempts
     * @param absent number of absent records
     * @param medical number of medical records
     * @param pending number of pending records
     * @author janith
     */
    public void setSummary(String title, String average, int attempts, int absent, int medical, int pending) {
        lblTitle.setText(title);
        lblAverage.setText("Avg: " + average);
        lblCounts.setText("Attempts: " + attempts + " | Absent: " + absent + " | Medical: " + medical + " | Pending: " + pending);
    }

    /**
     * Creates a styled label used inside the summary card.
     * @param text label text
     * @param style font style
     * @param size font size
     * @param color label color
     * @author janith
     */
    private JLabel createLabel(String text, int style, int size, Color color) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setFont(new Font(AppTheme.FONT_FAMILY, style, size));
        label.setForeground(color);
        return label;
    }
}
