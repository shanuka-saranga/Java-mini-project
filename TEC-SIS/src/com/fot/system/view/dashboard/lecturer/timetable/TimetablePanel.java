package com.fot.system.view.dashboard.lecturer.timetable;

import com.fot.system.config.AppTheme;
import com.fot.system.model.TimetableSession;
import com.fot.system.model.User;
import com.fot.system.service.TimetableService;
import com.fot.system.view.components.SectionCard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TimetablePanel extends JPanel {
    private static final String[] DAY_COLUMNS = {"Time", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    private final TimetableService timetableService;
    private final DefaultTableModel tableModel;
    private final JTable timetableTable;
    private final JPanel centerContentPanel;
    private final CardLayout centerCardLayout;

    private static final String TABLE_CARD = "TABLE";
    private static final String EMPTY_CARD = "EMPTY";

    public TimetablePanel(User user) {
        this.timetableService = new TimetableService();

        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.SURFACE_SOFT);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        add(createHeader(), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);

        centerCardLayout = new CardLayout();
        centerContentPanel = new JPanel(centerCardLayout);
        centerContentPanel.setOpaque(false);

        tableModel = new DefaultTableModel(DAY_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        timetableTable = new JTable(tableModel);
        timetableTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        timetableTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        timetableTable.setForeground(AppTheme.TEXT_DARK);
        timetableTable.setGridColor(AppTheme.BORDER_SOFT);
        timetableTable.setSelectionBackground(AppTheme.TABLE_SELECTION_BG);
        timetableTable.setSelectionForeground(AppTheme.TABLE_SELECTION_FG);
        timetableTable.getTableHeader().setBackground(AppTheme.TABLE_HEADER_BG);
        timetableTable.getTableHeader().setForeground(AppTheme.TABLE_HEADER_FG);
        timetableTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        timetableTable.setFillsViewportHeight(true);
        timetableTable.setRowSelectionAllowed(false);
        timetableTable.setCellSelectionEnabled(false);
        timetableTable.setDefaultRenderer(Object.class, new TimetableCellRenderer());

        timetableTable.getColumnModel().getColumn(0).setPreferredWidth(115);
        for (int i = 1; i < timetableTable.getColumnCount(); i++) {
            timetableTable.getColumnModel().getColumn(i).setPreferredWidth(175);
        }

        JScrollPane tableScrollPane = new JScrollPane(timetableTable);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, true));
        tableScrollPane.getViewport().setBackground(AppTheme.CARD_BG);
        tableScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        tableScrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        SectionCard tableCard = new SectionCard(
                "Weekly Timetable",
                "This timetable displays all sessions for only ict department 2nd year this semester."
        );
        tableCard.setContent(tableScrollPane);

        centerContentPanel.add(tableCard, TABLE_CARD);
        centerContentPanel.add(createEmptyState(), EMPTY_CARD);

        content.add(centerContentPanel, BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);

        loadTimetable();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 8));
        header.setOpaque(false);

        JLabel title = new JLabel("Timetables");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(AppTheme.TEXT_DARK);

        JLabel subtitle = new JLabel("This timetable displays all sessions for only ict department 2nd year this semester.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(AppTheme.TEXT_SUBTLE);

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        return header;
    }

    private void loadTimetable() {
        SwingWorker<List<TimetableSession>, Void> worker = new SwingWorker<List<TimetableSession>, Void>() {
            @Override
            protected List<TimetableSession> doInBackground() {
                return timetableService.getAllTimetableSessions();
            }

            @Override
            protected void done() {
                try {
                    List<TimetableSession> sessions = get();
                    renderTimetable(sessions);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(
                            TimetablePanel.this,
                            "Unable to load timetable. Make sure timetable sessions are available.",
                            "Timetable Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void renderTimetable(List<TimetableSession> sessions) {
        if (sessions == null || sessions.isEmpty()) {
            tableModel.setRowCount(0);
            centerCardLayout.show(centerContentPanel, EMPTY_CARD);
        } else {
            populateGrid(sessions);
            adjustRowHeights();
            if (timetableTable.getRowCount() > 0) {
                timetableTable.setRowSelectionInterval(0, 0);
            }
            centerCardLayout.show(centerContentPanel, TABLE_CARD);
        }
    }

    private void populateGrid(List<TimetableSession> sessions) {
        tableModel.setRowCount(0);

        List<String> timeSlots = collectTimeSlots(sessions);
        Map<String, Integer> timeSlotRows = new LinkedHashMap<>();

        for (String timeSlot : timeSlots) {
            Object[] rowData = new Object[DAY_COLUMNS.length];
            rowData[0] = timeSlot;
            for (int i = 1; i < DAY_COLUMNS.length; i++) {
                rowData[i] = "";
            }
            tableModel.addRow(rowData);
            timeSlotRows.put(timeSlot, tableModel.getRowCount() - 1);
        }

        for (TimetableSession session : sessions) {
            String timeSlot = buildTimeRange(session);
            Integer rowIndex = timeSlotRows.get(timeSlot);
            int dayColumn = resolveDayColumn(session.getDay());

            if (rowIndex == null || dayColumn < 1) {
                continue;
            }

            String currentValue = valueOrDash((String) tableModel.getValueAt(rowIndex, dayColumn));
            String sessionText = buildCellText(session);

            if ("-".equals(currentValue)) {
                tableModel.setValueAt(sessionText, rowIndex, dayColumn);
            } else {
                tableModel.setValueAt(currentValue + "\n\n" + sessionText, rowIndex, dayColumn);
            }
        }
    }

    private List<String> collectTimeSlots(List<TimetableSession> sessions) {
        Map<String, String> orderedSlots = new LinkedHashMap<>();
        for (TimetableSession session : sessions) {
            String slot = buildTimeRange(session);
            orderedSlots.put(slot, slot);
        }
        return new ArrayList<>(orderedSlots.values());
    }

    private int resolveDayColumn(String day) {
        String normalized = normalize(day).toUpperCase();
        return switch (normalized) {
            case "MONDAY" -> 1;
            case "TUESDAY" -> 2;
            case "WEDNESDAY" -> 3;
            case "THURSDAY" -> 4;
            case "FRIDAY" -> 5;
            default -> -1;
        };
    }

    private String buildCellText(TimetableSession session) {
        return valueOrDash(session.getCourseCode()) + "\n"
                + valueOrDash(session.getCourseName()) + "\n"
                + valueOrDash(session.getVenue()) + "\n"
                + valueOrDash(session.getSessionType());
    }

    private void adjustRowHeights() {
        for (int row = 0; row < timetableTable.getRowCount(); row++) {
            int maxHeight = 56;
            for (int col = 0; col < timetableTable.getColumnCount(); col++) {
                Component comp = timetableTable.prepareRenderer(timetableTable.getCellRenderer(row, col), row, col);
                maxHeight = Math.max(maxHeight, comp.getPreferredSize().height + 10);
            }
            timetableTable.setRowHeight(row, maxHeight);
        }
    }

    private Component createEmptyState() {
        SectionCard card = new SectionCard(
                "No Timetable Sessions",
                "Your teaching timetable is currently empty. Sessions will appear here once they are assigned."
        );

        JPanel content = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        content.setOpaque(false);

        JLabel textLabel = new JLabel("No timetable entries were found for your lecturer account.");
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textLabel.setForeground(AppTheme.TEXT_SUBTLE);

        content.add(textLabel);
        card.setContent(content);
        return card;
    }

    private String buildTimeRange(TimetableSession session) {
        return formatTime(session.getStartTime()) + " - " + formatTime(session.getEndTime());
    }

    private String formatTime(String value) {
        String normalized = normalize(value);
        if (normalized.isEmpty()) {
            return "-";
        }
        return normalized.length() >= 5 ? normalized.substring(0, 5) : normalized;
    }

    private String formatDay(String day) {
        String normalized = normalize(day).toLowerCase();
        if (normalized.isEmpty()) {
            return "-";
        }
        return Character.toUpperCase(normalized.charAt(0)) + normalized.substring(1);
    }

    private String valueOrDash(String value) {
        String normalized = normalize(value);
        return normalized.isEmpty() ? "-" : normalized;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private static class TimetableCellRenderer extends JTextArea implements javax.swing.table.TableCellRenderer {
        private TimetableCellRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            setBorder(new EmptyBorder(8, 8, 8, 8));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());
            setForeground(column == 0 ? AppTheme.TEXT_DARK : AppTheme.TEXT_SUBTLE);

            if (column == 0) {
                setBackground(AppTheme.CARD_MUTED_BG);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
            } else {
                setBackground(AppTheme.CARD_BG);
                setFont(new Font("Segoe UI", Font.PLAIN, 12));
            }

            return this;
        }
    }
}
