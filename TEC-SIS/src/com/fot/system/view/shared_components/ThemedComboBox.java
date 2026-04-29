package com.fot.system.view.shared_components;

import com.fot.system.config.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;

public class ThemedComboBox<T> extends JComboBox<T> {
    private static final int DEFAULT_HEIGHT = 38;
    private static final int ARROW_BUTTON_WIDTH = 34;
    private static final int MAX_VISIBLE_ROWS = 8;

    public ThemedComboBox() {
        super();
        applyTheme();
    }

    public ThemedComboBox(T[] items) {
        super(items);
        applyTheme();
    }

    private void applyTheme() {
        setFont(AppTheme.FORM_INPUT_FONT);
        setForeground(AppTheme.TEXT_DARK);
        setBackground(AppTheme.BG_LIGHT);
        setBorder(BorderFactory.createCompoundBorder(
                AppTheme.lineBorder(AppTheme.BORDER_MUTED),
                new EmptyBorder(0, 10, 0, 8)
        ));
        setPreferredSize(new Dimension(0, DEFAULT_HEIGHT));
        setMinimumSize(new Dimension(120, DEFAULT_HEIGHT));
        updateVisibleRowCount();
        setOpaque(true);
        setFocusable(false);
        setUI(new FlatComboBoxUi());
        setRenderer(new FlatComboBoxRenderer());
    }

    @Override
    public void setModel(ComboBoxModel<T> model) {
        super.setModel(model);
        updateVisibleRowCount();
    }

    @Override
    public void addItem(T item) {
        super.addItem(item);
        updateVisibleRowCount();
    }

    @Override
    public void removeItem(Object anObject) {
        super.removeItem(anObject);
        updateVisibleRowCount();
    }

    @Override
    public void removeItemAt(int anIndex) {
        super.removeItemAt(anIndex);
        updateVisibleRowCount();
    }

    @Override
    public void removeAllItems() {
        super.removeAllItems();
        updateVisibleRowCount();
    }

    private void updateVisibleRowCount() {
        int itemCount = getItemCount();
        int rows = Math.max(1, Math.min(MAX_VISIBLE_ROWS, itemCount));
        setMaximumRowCount(rows);
    }

    private static final class FlatComboBoxUi extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton button = new JButton();
            button.setFocusable(false);
            button.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, AppTheme.BORDER_LIGHT));
            button.setBackground(AppTheme.SURFACE_MUTED);
            button.setPreferredSize(new Dimension(ARROW_BUTTON_WIDTH, DEFAULT_HEIGHT));
            button.setContentAreaFilled(true);
            button.setOpaque(true);
            button.setIcon(new DownArrowIcon(8, 5, AppTheme.TEXT_SUBTLE));
            return button;
        }

        @Override
        protected ComboPopup createPopup() {
            BasicComboPopup popup = (BasicComboPopup) super.createPopup();
            popup.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, false));
            popup.getList().setSelectionBackground(AppTheme.TABLE_SELECTION_BG);
            popup.getList().setSelectionForeground(AppTheme.TEXT_DARK);
            popup.getList().setBackground(AppTheme.BG_LIGHT);
            popup.getList().setForeground(AppTheme.TEXT_DARK);
            popup.getList().setFixedCellHeight(32);
            return popup;
        }
    }

    private static final class FlatComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setBorder(new EmptyBorder(0, 10, 0, 8));
            label.setFont(AppTheme.FORM_INPUT_FONT);
            label.setForeground(isSelected ? AppTheme.TEXT_DARK : AppTheme.TEXT_DARK);
            label.setBackground(isSelected ? AppTheme.TABLE_SELECTION_BG : AppTheme.BG_LIGHT);
            return label;
        }
    }

    private static final class DownArrowIcon implements Icon {
        private final int width;
        private final int height;
        private final Color color;

        private DownArrowIcon(int width, int height, Color color) {
            this.width = width;
            this.height = height;
            this.color = color;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            int[] xs = {x, x + width / 2, x + width};
            int[] ys = {y, y + height, y};
            g2.fillPolygon(xs, ys, 3);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return width;
        }

        @Override
        public int getIconHeight() {
            return height;
        }
    }
}
