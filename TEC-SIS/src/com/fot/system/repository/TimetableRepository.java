package com.fot.system.repository;

import com.fot.system.config.DBConnection;
import com.fot.system.model.TimetableEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TimetableRepository {
    private final Connection conn;

    public TimetableRepository() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public Map<String, List<TimetableEntry>> findWeeklyTimetable() {
        Map<String, List<TimetableEntry>> timetable = createEmptyWeekMap();
        String sql = """
                SELECT
                    ts.session_day,
                    ts.start_time,
                    ts.end_time,
                    c.course_name,
                    ts.venue,
                    CONCAT(u.first_name, ' ', u.last_name) AS lecturer_name
                FROM timetable_sessions ts
                INNER JOIN courses c ON ts.course_id = c.id
                LEFT JOIN users u ON ts.lecturer_id = u.id
                ORDER BY
                    FIELD(ts.session_day, 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY'),
                    ts.start_time,
                    c.course_code
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String dayKey = formatDay(rs.getString("session_day"));
                TimetableEntry entry = new TimetableEntry(
                        dayKey,
                        formatTimeRange(rs.getString("start_time"), rs.getString("end_time")),
                        rs.getString("course_name"),
                        rs.getString("venue"),
                        valueOrDash(rs.getString("lecturer_name"))
                );
                timetable.computeIfAbsent(dayKey, key -> new ArrayList<>()).add(entry);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load timetable: " + e.getMessage(), e);
        }

        return timetable;
    }

    private Map<String, List<TimetableEntry>> createEmptyWeekMap() {
        Map<String, List<TimetableEntry>> timetable = new LinkedHashMap<>();
        timetable.put("Monday", new ArrayList<>());
        timetable.put("Tuesday", new ArrayList<>());
        timetable.put("Wednesday", new ArrayList<>());
        timetable.put("Thursday", new ArrayList<>());
        timetable.put("Friday", new ArrayList<>());
        return timetable;
    }

    private String formatDay(String dbDay) {
        String lower = dbDay == null ? "" : dbDay.trim().toLowerCase();
        if (lower.isEmpty()) {
            return "-";
        }
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    private String formatTimeRange(String start, String end) {
        return shortTime(start) + " - " + shortTime(end);
    }

    private String shortTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "-";
        }
        return value.length() >= 5 ? value.substring(0, 5) : value;
    }

    private String valueOrDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }
}
