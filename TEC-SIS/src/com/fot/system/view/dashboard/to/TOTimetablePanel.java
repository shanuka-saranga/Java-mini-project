package com.fot.system.view.dashboard.to;

import com.fot.system.config.AppTheme;
import com.fot.system.model.TimetableSession;
import com.fot.system.model.User;
import com.fot.system.service.TimetableService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

public class TOTimetablePanel extends JPanel {

    private static final List<String> WEEKDAYS =
            List.of("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY");

    private final TimetableService timetableService = new TimetableService();
    private final JPanel timetableContainer = new JPanel(new BorderLayout());

    public TOTimetablePanel(User user) {

        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.SURFACE_SOFT);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        add(createHeader(), BorderLayout.NORTH);

        timetableContainer.setOpaque(false);
        add(timetableContainer, BorderLayout.CENTER);

        loadTimetableData();
    }

    // ---------------- HEADER ----------------
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 8));
        header.setOpaque(false);

        JLabel title = label("Timetable", 26, Font.BOLD, AppTheme.TEXT_DARK);
        JLabel subtitle = label(
                "View the weekly timetable for all courses in a real timetable layout.",
                14, Font.PLAIN, AppTheme.TEXT_SUBTLE
        );

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        return header;
    }


    private void loadTimetableData() {
        SwingWorker<List<TimetableSession>, Void> worker = new SwingWorker<>() {
            protected List<TimetableSession> doInBackground() {
                return timetableService.getAllTimetableSessions();
            }

            protected void done() {
                try {
                    renderTimetable(get());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            TOTimetablePanel.this,
                            "Failed to load timetable sessions.",
                            "Timetable Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }


    private void renderTimetable(List<TimetableSession> sessions) {

        timetableContainer.removeAll();

        if (sessions == null || sessions.isEmpty()) {
            timetableContainer.add(label("No timetable sessions available.",
                    14, Font.PLAIN, AppTheme.TEXT_SUBTLE), BorderLayout.NORTH);
            refresh();
            return;
        }

        List<TimetableSession> filtered = sessions.stream()
                .filter(s -> WEEKDAYS.contains(norm(s.getDay())))
                .sorted(Comparator
                        .comparing((TimetableSession s) -> WEEKDAYS.indexOf(norm(s.getDay())))
                        .thenComparing(s -> normTime(s.getStartTime()))
                        .thenComparing(s -> norm(s.getCourseCode())))
                .toList();

        List<String> timeSlots = buildTimeSlots(filtered);
        Map<String, Map<String, List<TimetableSession>>> grouped = group(filtered);

        JPanel grid = new JPanel(new GridLayout(timeSlots.size() + 1, WEEKDAYS.size() + 1, 8, 8));
        grid.setOpaque(false);

        grid.add(cell("Time", AppTheme.CARD_BG, true));

        for (String d : WEEKDAYS)
            grid.add(cell(toTitle(d), AppTheme.TABLE_HEADER_BG, true));

        for (String time : timeSlots) {
            grid.add(cell(time, AppTheme.CARD_BG, true));

            for (String day : WEEKDAYS) {
                grid.add(sessionCell(
                        grouped.getOrDefault(time, Map.of())
                                .getOrDefault(day, List.of())
                ));
            }
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, true));
        scroll.getViewport().setBackground(AppTheme.SURFACE_SOFT);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(AppTheme.CARD_BG);
        card.setBorder(new EmptyBorder(18, 18, 18, 18));
        card.add(scroll);

        timetableContainer.add(card);
        refresh();
    }

    // ---------------- GROUPING ----------------
    private List<String> buildTimeSlots(List<TimetableSession> sessions) {
        LinkedHashSet<String> slots = new LinkedHashSet<>();

        for (TimetableSession s : sessions) {
            slots.add(format(s.getStartTime()) + " - " + format(s.getEndTime()));
        }
        return new ArrayList<>(slots);
    }

    private Map<String, Map<String, List<TimetableSession>>> group(List<TimetableSession> sessions) {
        Map<String, Map<String, List<TimetableSession>>> map = new LinkedHashMap<>();

        for (TimetableSession s : sessions) {
            String time = format(s.getStartTime()) + " - " + format(s.getEndTime());
            String day = norm(s.getDay());

            map.computeIfAbsent(time, k -> new LinkedHashMap<>())
                    .computeIfAbsent(day, k -> new ArrayList<>())
                    .add(s);
        }
        return map;
    }


    private JPanel sessionCell(List<TimetableSession> sessions) {

        JPanel panel = boxPanel(210, 92);

        if (sessions.isEmpty()) {
            panel.add(centerLabel("-"));
            return panel;
        }

        for (TimetableSession s : sessions) {
            JPanel card = boxPanel(0, 0);
            card.setBackground(AppTheme.CARD_MUTED_BG);

            card.add(label(value(s.getCourseCode()), 13, Font.BOLD, AppTheme.TEXT_DARK));
            card.add(label(value(s.getCourseName()), 12, Font.PLAIN, AppTheme.TEXT_DARK));
            card.add(label(value(s.getVenue()), 12, Font.PLAIN, AppTheme.TEXT_SUBTLE));
            card.add(label(
                    value(s.getSessionType()) + " | " + value(s.getLecturerName()),
                    11, Font.PLAIN, AppTheme.TEXT_SUBTLE
            ));

            panel.add(card);
            panel.add(Box.createVerticalStrut(6));
        }

        return panel;
    }

    private JPanel cell(String text, Color bg, boolean center) {
        JPanel p = boxPanel(135, 92);
        p.setBackground(bg);
        p.add(center ? centerLabel(text) : label(text, 13, Font.BOLD, AppTheme.TEXT_DARK));
        return p;
    }

    private JPanel boxPanel(int w, int h) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        if (w > 0 && h > 0) p.setPreferredSize(new Dimension(w, h));
        return p;
    }

    private JLabel label(String t, int size, int style, Color c) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", style, size));
        l.setForeground(c);
        return l;
    }

    private JLabel centerLabel(String t) {
        JLabel l = label(t, 13, Font.PLAIN, AppTheme.TEXT_MUTED);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    private void refresh() {
        timetableContainer.revalidate();
        timetableContainer.repaint();
    }

    private String norm(String v) {
        return v == null ? "" : v.trim().toUpperCase();
    }

    private String normTime(String v) {
        return v == null ? "" : v.trim();
    }

    private String format(String v) {
        if (v == null || v.isEmpty()) return "-";
        return v.length() >= 5 ? v.substring(0, 5) : v;
    }

    private String value(String v) {
        return (v == null || v.isBlank()) ? "-" : v.trim();
    }

    private String toTitle(String v) {
        String s = norm(v).toLowerCase();
        return s.isEmpty() ? "-" : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}