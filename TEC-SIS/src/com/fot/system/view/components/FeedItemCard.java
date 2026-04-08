package com.fot.system.view.components;

import com.fot.system.config.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FeedItemCard extends JPanel {

    public FeedItemCard(String titleText, String bodyText, String metaText, Color indicatorColor, int fixedHeight) {
        this(titleText, bodyText, metaText, indicatorColor, fixedHeight, true);
    }

    public FeedItemCard(String titleText, String bodyText, String metaText, int fixedHeight) {
        this(titleText, bodyText, metaText, AppTheme.PRIMARY, fixedHeight, false);
    }

    private FeedItemCard(String titleText, String bodyText, String metaText, Color indicatorColor, int fixedHeight, boolean showIndicator) {
        setLayout(new BorderLayout(0, 10));
        setBackground(AppTheme.CARD_MUTED_BG);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.CARD_BORDER, 1, true),
                new EmptyBorder(14, 14, 14, 14)
        ));
        setPreferredSize(new Dimension(0, fixedHeight));
        setMinimumSize(new Dimension(0, fixedHeight));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, fixedHeight));

        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(false);

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(AppTheme.TEXT_DARK);

        JTextArea body = new JTextArea(bodyText);
        body.setEditable(false);
        body.setLineWrap(true);
        body.setWrapStyleWord(true);
        body.setOpaque(false);
        body.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        body.setForeground(AppTheme.TEXT_SUBTLE);
        body.setRows(2);

        JLabel meta = new JLabel(metaText);
        meta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        meta.setForeground(AppTheme.TEXT_MUTED);

        header.add(title, BorderLayout.CENTER);
        if (showIndicator) {
            JPanel indicator = new JPanel();
            indicator.setOpaque(false);
            indicator.setPreferredSize(new Dimension(18, 18));
            indicator.setMinimumSize(new Dimension(18, 18));
            indicator.setMaximumSize(new Dimension(18, 18));
            indicator.add(new IndicatorDot(indicatorColor));
            header.add(indicator, BorderLayout.EAST);
        }

        add(header, BorderLayout.NORTH);
        add(body, BorderLayout.CENTER);
        add(meta, BorderLayout.SOUTH);
    }

    private static class IndicatorDot extends JComponent {
        private final Color color;

        private IndicatorDot(Color color) {
            this.color = color;
            setPreferredSize(new Dimension(10, 10));
            setMinimumSize(new Dimension(10, 10));
            setMaximumSize(new Dimension(10, 10));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillOval(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }
}
