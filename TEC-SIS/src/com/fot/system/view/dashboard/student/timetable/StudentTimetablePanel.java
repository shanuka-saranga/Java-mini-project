package com.fot.system.view.dashboard.student.timetable;

import com.fot.system.config.AppTheme;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.service.TimetableService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StudentTimetablePanel extends JPanel {
    private static final List<String> WEEKDAYS = List.of("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY");

    private final TimetableService timetableService;
    private final JPanel timetableContainer;

    public StudentTimetablePanel(User user) {
        this.timetableService = new TimetableService();

        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.SURFACE_SOFT);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        add(createHeader(), BorderLayout.NORTH);

        timetableContainer = new JPanel(new BorderLayout());
        timetableContainer.setOpaque(false);
        add(timetableContainer, BorderLayout.CENTER);

        loadTimetableData();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 8));
        header.setOpaque(false);

        JLabel title = new JLabel("Timetable");
        title.setFont(AppTheme.fontBold(26));
        title.setForeground(AppTheme.TEXT_DARK);

        JLabel subtitle = new JLabel("View the weekly timetable in a real timetable layout with weekdays across the top.");
        subtitle.setFont(AppTheme.fontPlain(14));
        subtitle.setForeground(AppTheme.TEXT_SUBTLE);

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        return header;
    }

    private void loadTimetableData() {
        SwingWorker<List<TimetableSession>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<TimetableSession> doInBackground() {
                return timetableService.getAllTimetableSessions();
            }

            @Override
            protected void done() {
                try {
                    renderTimetable(get());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            StudentTimetablePanel.this,
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
            JLabel empty = new JLabel("No timetable sessions available.");
            empty.setFont(AppTheme.fontPlain(14));
            empty.setForeground(AppTheme.TEXT_SUBTLE);
            timetableContainer.add(empty, BorderLayout.NORTH);
            timetableContainer.revalidate();
            timetableContainer.repaint();
            return;
        }

        List<TimetableSession> weekdaySessions = sessions.stream()
                .filter(session -> WEEKDAYS.contains(normalize(session.getDay())))
                .sorted(Comparator
                        .comparing((TimetableSession session) -> WEEKDAYS.indexOf(normalize(session.getDay())))
                        .thenComparing(session -> normalizeTime(session.getStartTime()))
                        .thenComparing(session -> normalize(session.getCourseCode())))
                .toList();

        List<String> timeSlots = buildTimeSlots(weekdaySessions);
        Map<String, Map<String, List<TimetableSession>>> groupedSessions = groupSessions(weekdaySessions);

        JPanel grid = new JPanel(new GridLayout(timeSlots.size() + 1, WEEKDAYS.size() + 1, 8, 8));
        grid.setOpaque(false);

        grid.add(createCornerCell());
        for (String day : WEEKDAYS) {
            grid.add(createDayHeader(day));
        }

        for (String timeSlot : timeSlots) {
            grid.add(createTimeCell(timeSlot));
            for (String day : WEEKDAYS) {
                List<TimetableSession> cellSessions = groupedSessions
                        .getOrDefault(timeSlot, Map.of())
                        .getOrDefault(day, List.of());
                grid.add(createSessionCell(cellSessions));
            }
        }

        JScrollPane scrollPane = new JScrollPane(grid);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, false),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        scrollPane.getViewport().setBackground(AppTheme.SURFACE_SOFT);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel contentCard = new JPanel(new BorderLayout());
        contentCard.setBackground(AppTheme.CARD_BG);
        contentCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, false),
                new EmptyBorder(18, 18, 18, 18)
        ));
        contentCard.add(scrollPane, BorderLayout.CENTER);

        timetableContainer.add(contentCard, BorderLayout.CENTER);
        timetableContainer.revalidate();
        timetableContainer.repaint();
    }

    private List<String> buildTimeSlots(List<TimetableSession> sessions) {
        Set<String> slots = new LinkedHashSet<>();
        for (TimetableSession session : sessions) {
            slots.add(formatTime(normalizeTime(session.getStartTime())) + " - " + formatTime(normalizeTime(session.getEndTime())));
        }
        return new ArrayList<>(slots);
    }

    private Map<String, Map<String, List<TimetableSession>>> groupSessions(List<TimetableSession> sessions) {
        Map<String, Map<String, List<TimetableSession>>> grouped = new LinkedHashMap<>();

        for (TimetableSession session : sessions) {
            String timeSlot = formatTime(normalizeTime(session.getStartTime())) + " - " + formatTime(normalizeTime(session.getEndTime()));
            String day = normalize(session.getDay());

            grouped
                    .computeIfAbsent(timeSlot, ignored -> new LinkedHashMap<>())
                    .computeIfAbsent(day, ignored -> new ArrayList<>())
                    .add(session);
        }

        return grouped;
    }

    private JComponent createCornerCell() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppTheme.CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, false),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JLabel label = new JLabel("Time");
        label.setFont(AppTheme.fontBold(14));
        label.setForeground(AppTheme.TEXT_DARK);
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private JComponent createDayHeader(String day) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppTheme.TABLE_HEADER_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.PRIMARY_ACTIVE, 1, false),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JLabel label = new JLabel(toTitle(day));
        label.setFont(AppTheme.fontBold(14));
        label.setForeground(AppTheme.TEXT_LIGHT);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private JComponent createTimeCell(String timeSlot) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppTheme.CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, false),
                new EmptyBorder(14, 10, 14, 10)
        ));
        panel.setPreferredSize(new Dimension(135, 92));

        JLabel label = new JLabel(timeSlot);
        label.setFont(AppTheme.fontBold(13));
        label.setForeground(AppTheme.TEXT_DARK);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private JComponent createSessionCell(List<TimetableSession> sessions) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(AppTheme.CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, false),
                new EmptyBorder(10, 10, 10, 10)
        ));
        panel.setPreferredSize(new Dimension(210, 92));

        if (sessions == null || sessions.isEmpty()) {
            JLabel empty = new JLabel("-");
            empty.setFont(AppTheme.fontPlain(13));
            empty.setForeground(AppTheme.TEXT_MUTED);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(Box.createVerticalGlue());
            panel.add(empty);
            panel.add(Box.createVerticalGlue());
            return panel;
        }

        for (int i = 0; i < sessions.size(); i++) {
            TimetableSession session = sessions.get(i);

            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBackground(AppTheme.CARD_MUTED_BG);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, false),
                    new EmptyBorder(8, 8, 8, 8)
            ));

            JLabel courseCode = new JLabel(valueOrDash(session.getCourseCode()));
            courseCode.setFont(AppTheme.fontBold(13));
            courseCode.setForeground(AppTheme.TEXT_DARK);

            JLabel courseName = new JLabel("<html><body style='width:160px'>" + valueOrDash(session.getCourseName()) + "</body></html>");
            courseName.setFont(AppTheme.fontPlain(12));
            courseName.setForeground(AppTheme.TEXT_DARK);

            JLabel venue = new JLabel(valueOrDash(session.getVenue()));
            venue.setFont(AppTheme.fontPlain(12));
            venue.setForeground(AppTheme.TEXT_SUBTLE);

            JLabel type = new JLabel(valueOrDash(session.getSessionType()));
            type.setFont(AppTheme.fontBold(11));
            type.setForeground(AppTheme.TEXT_SUBTLE);

            card.add(courseCode);
            card.add(Box.createVerticalStrut(4));
            card.add(courseName);
            card.add(Box.createVerticalStrut(4));
            card.add(venue);
            card.add(Box.createVerticalStrut(4));
            card.add(type);

            panel.add(card);
            if (i < sessions.size() - 1) {
                panel.add(Box.createVerticalStrut(8));
            }
        }

        return panel;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }

    private String normalizeTime(String value) {
        return value == null ? "" : value.trim();
    }

    private String formatTime(String value) {
        return value != null && value.length() >= 5 ? value.substring(0, 5) : valueOrDash(value);
    }

    private String toTitle(String value) {
        String normalized = normalize(value).toLowerCase();
        return normalized.isEmpty() ? "-" : Character.toUpperCase(normalized.charAt(0)) + normalized.substring(1);
    }

    private String valueOrDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }
}
