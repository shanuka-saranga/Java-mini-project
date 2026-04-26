package com.fot.system.view.components;

import com.fot.system.config.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ThemedDatePicker extends JPanel {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy");
    private static final Dimension INPUT_SIZE = new Dimension(0, 38);
    private static final String[] WEEK_DAY_LABELS = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    private final JTextField textField;
    private final JButton calendarButton;
    private final JPopupMenu popupMenu = new JPopupMenu();
    private final JLabel monthLabel = new JLabel("", SwingConstants.CENTER);
    private final JPanel daysGrid = new JPanel(new GridLayout(6, 7, 4, 4));
    private final List<Runnable> dateChangeListeners = new ArrayList<>();
    private YearMonth viewingMonth;
    private LocalDate selectedDate;
    private boolean popupOpen;

    public ThemedDatePicker() {
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(AppTheme.BG_LIGHT);
        setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_MUTED, 1, false));
        setPreferredSize(INPUT_SIZE);
        setMinimumSize(new Dimension(120, INPUT_SIZE.height));

        textField = new JTextField();
        textField.setFont(AppTheme.fontPlain(14));
        textField.setForeground(AppTheme.TEXT_DARK);
        textField.setBackground(AppTheme.BG_LIGHT);
        textField.setBorder(new EmptyBorder(7, 10, 7, 10));
        textField.addActionListener(e -> syncFromTextField());
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                syncFromTextField();
            }
        });

        calendarButton = createCalendarButton();
        add(textField, BorderLayout.CENTER);
        add(calendarButton, BorderLayout.EAST);

        initializePopup();
    }

    public String getText() {
        return textField.getText() == null ? "" : textField.getText().trim();
    }

    public void setText(String value) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isEmpty()) {
            textField.setText("");
            selectedDate = null;
            viewingMonth = YearMonth.now();
            fireDateChanged();
            return;
        }

        try {
            LocalDate localDate = LocalDate.parse(normalized, DATE_FORMATTER);
            selectedDate = localDate;
            viewingMonth = YearMonth.from(localDate);
            textField.setText(normalized);
        } catch (DateTimeParseException ex) {
            textField.setText(normalized);
            selectedDate = null;
            viewingMonth = YearMonth.now();
        }
        fireDateChanged();
    }

    public void setEditable(boolean editable) {
        textField.setEditable(editable);
        calendarButton.setEnabled(editable);
    }

    public void addDateChangeListener(Runnable listener) {
        if (listener != null) {
            dateChangeListeners.add(listener);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textField.setEnabled(enabled);
        calendarButton.setEnabled(enabled);
    }

    private JButton createCalendarButton() {
        JButton button = new JButton(new CalendarIcon(14, AppTheme.TEXT_SUBTLE));
        button.setFocusable(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBackground(AppTheme.SURFACE_MUTED);
        button.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, AppTheme.BORDER_LIGHT));
        button.setPreferredSize(new Dimension(36, INPUT_SIZE.height));
        button.addActionListener(e -> togglePopup());
        return button;
    }

    private void initializePopup() {
        popupMenu.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, false));
        popupMenu.setLayout(new BorderLayout(0, 8));

        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setBorder(new EmptyBorder(10, 10, 10, 10));
        content.setBackground(AppTheme.BG_LIGHT);

        JPanel header = new JPanel(new BorderLayout(6, 0));
        header.setOpaque(false);
        JButton prevButton = createHeaderButton("<", -1);
        JButton nextButton = createHeaderButton(">", 1);

        monthLabel.setFont(AppTheme.fontBold(14));
        monthLabel.setForeground(AppTheme.TEXT_DARK);
        header.add(prevButton, BorderLayout.WEST);
        header.add(monthLabel, BorderLayout.CENTER);
        header.add(nextButton, BorderLayout.EAST);

        JPanel weekHeader = new JPanel(new GridLayout(1, 7, 4, 0));
        weekHeader.setOpaque(false);
        for (String weekDay : WEEK_DAY_LABELS) {
            JLabel label = new JLabel(weekDay, SwingConstants.CENTER);
            label.setFont(AppTheme.fontBold(12));
            label.setForeground(AppTheme.TEXT_SUBTLE);
            weekHeader.add(label);
        }

        daysGrid.setOpaque(false);
        content.add(header, BorderLayout.NORTH);
        content.add(weekHeader, BorderLayout.CENTER);
        content.add(daysGrid, BorderLayout.SOUTH);
        popupMenu.add(content, BorderLayout.CENTER);

        popupMenu.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                popupOpen = true;
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                popupOpen = false;
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                popupOpen = false;
            }
        });
    }

    private JButton createHeaderButton(String text, int monthOffset) {
        JButton button = new JButton(text);
        button.setFocusable(false);
        button.setFont(AppTheme.fontBold(13));
        button.setForeground(AppTheme.TEXT_DARK);
        button.setBackground(AppTheme.SURFACE_MUTED);
        button.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, false));
        button.addActionListener(e -> {
            if (viewingMonth == null) {
                viewingMonth = YearMonth.now();
            }
            viewingMonth = viewingMonth.plusMonths(monthOffset);
            rebuildCalendar();
        });
        return button;
    }

    private void togglePopup() {
        if (!isEnabled()) {
            return;
        }
        if (popupOpen) {
            popupMenu.setVisible(false);
            return;
        }

        syncFromTextField();
        if (viewingMonth == null) {
            viewingMonth = selectedDate == null ? YearMonth.now() : YearMonth.from(selectedDate);
        }
        rebuildCalendar();
        popupMenu.show(this, 0, getHeight());
    }

    private void rebuildCalendar() {
        monthLabel.setText(viewingMonth.atDay(1).format(MONTH_FORMATTER));
        daysGrid.removeAll();

        LocalDate firstOfMonth = viewingMonth.atDay(1);
        int startOffset = calculateStartOffset(firstOfMonth.getDayOfWeek());
        int daysInMonth = viewingMonth.lengthOfMonth();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < startOffset; i++) {
            daysGrid.add(createEmptyCell());
        }

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate cellDate = viewingMonth.atDay(day);
            daysGrid.add(createDayButton(cellDate, cellDate.equals(today), cellDate.equals(selectedDate)));
        }

        int remainingCells = 42 - (startOffset + daysInMonth);
        for (int i = 0; i < remainingCells; i++) {
            daysGrid.add(createEmptyCell());
        }

        daysGrid.revalidate();
        daysGrid.repaint();
    }

    private int calculateStartOffset(DayOfWeek dayOfWeek) {
        return dayOfWeek.getValue() - 1;
    }

    private Component createEmptyCell() {
        JPanel empty = new JPanel();
        empty.setOpaque(false);
        return empty;
    }

    private JButton createDayButton(LocalDate date, boolean isToday, boolean isSelected) {
        JButton button = new JButton(String.valueOf(date.getDayOfMonth()));
        button.setFocusable(false);
        button.setFont(AppTheme.fontPlain(13));
        button.setOpaque(true);
        button.setBackground(AppTheme.BG_LIGHT);
        button.setForeground(AppTheme.TEXT_DARK);
        button.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, false));

        if (isToday) {
            button.setBorder(BorderFactory.createLineBorder(AppTheme.PRIMARY, 1, false));
        }
        if (isSelected) {
            button.setBackground(AppTheme.TABLE_SELECTION_BG);
            button.setForeground(AppTheme.TABLE_SELECTION_FG);
        }

        button.addActionListener(e -> {
            selectedDate = date;
            textField.setText(date.format(DATE_FORMATTER));
            viewingMonth = YearMonth.from(date);
            popupMenu.setVisible(false);
            fireDateChanged();
        });

        return button;
    }

    private void syncFromTextField() {
        String value = getText();
        if (value.isEmpty()) {
            selectedDate = null;
            viewingMonth = YearMonth.now();
            fireDateChanged();
            return;
        }
        try {
            selectedDate = LocalDate.parse(value, DATE_FORMATTER);
            viewingMonth = YearMonth.from(selectedDate);
            textField.setText(selectedDate.format(DATE_FORMATTER));
        } catch (DateTimeParseException ignored) {
            selectedDate = null;
            viewingMonth = YearMonth.now();
        }
        fireDateChanged();
    }

    private void fireDateChanged() {
        for (Runnable listener : new ArrayList<>(dateChangeListeners)) {
            listener.run();
        }
    }

    private static class CalendarIcon implements Icon {
        private final int size;
        private final Color color;

        private CalendarIcon(int size, Color color) {
            this.size = size;
            this.color = color;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.6f));

            int w = size;
            int h = size;
            g2.drawRoundRect(x, y + 2, w, h - 2, 3, 3);
            g2.drawLine(x, y + 5, x + w, y + 5);
            g2.drawLine(x + 4, y, x + 4, y + 4);
            g2.drawLine(x + w - 4, y, x + w - 4, y + 4);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }
}
