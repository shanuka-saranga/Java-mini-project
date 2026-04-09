package com.fot.system.service;

import com.fot.system.model.TimetableSession;
import com.fot.system.model.TimetableSessionRequest;
import com.fot.system.model.Course;
import com.fot.system.repository.TimetableRepository;

import java.sql.Time;
import java.util.List;
import java.util.Set;

public class TimetableService {
    private static final Set<String> VALID_TYPES = Set.of("THEORY", "PRACTICAL");
    private static final Set<String> VALID_DAYS = Set.of("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY");

    private final TimetableRepository timetableRepository;
    private final CourseService courseService;

    public TimetableService() {
        this.timetableRepository = new TimetableRepository();
        this.courseService = new CourseService();
    }

    public List<TimetableSession> getAllTimetableSessions() {
        return timetableRepository.getAllTimetableSessions();
    }

    public List<TimetableSession> getTimetableByLecturer(int lecturerId) {
        if (lecturerId <= 0) {
            throw new RuntimeException("Invalid lecturer ID.");
        }
        return timetableRepository.getTimetableByLecturer(lecturerId);
    }

    public TimetableSession getSessionById(int sessionId) {
        if (sessionId <= 0) {
            throw new RuntimeException("Invalid timetable session ID.");
        }
        return timetableRepository.findById(sessionId);
    }

    public TimetableSession addSession(TimetableSessionRequest request) {
        TimetableSession session = buildValidatedSession(request, false);
        if (!timetableRepository.save(session)) {
            throw new RuntimeException("Timetable session save failed.");
        }
        return timetableRepository.findById(session.getId());
    }

    public TimetableSession updateSession(TimetableSessionRequest request) {
        TimetableSession session = buildValidatedSession(request, true);
        if (!timetableRepository.update(session)) {
            throw new RuntimeException("Timetable session update failed.");
        }
        return timetableRepository.findById(session.getId());
    }

    public void deleteSession(int sessionId) {
        if (sessionId <= 0) {
            throw new RuntimeException("Invalid timetable session ID.");
        }
        if (!timetableRepository.deleteById(sessionId)) {
            throw new RuntimeException("Timetable session delete failed.");
        }
    }

    private TimetableSession buildValidatedSession(TimetableSessionRequest request, boolean requireId) {
        if (request == null) {
            throw new RuntimeException("Timetable request cannot be null.");
        }

        TimetableSession session = new TimetableSession();
        session.setId(request.getSessionId());
        session.setCourseId(parsePositiveInt(request.getCourseId(), "Course is required."));
        Course course = courseService.getCourseById(session.getCourseId());
        if (course == null) {
            throw new RuntimeException("Selected course was not found.");
        }
        session.setLecturerId(parseOptionalPositiveInt(request.getLecturerId(), "Lecturer is invalid."));

        String type = normalize(request.getSessionType()).toUpperCase();
        if (!VALID_TYPES.contains(type)) {
            throw new RuntimeException("Session type is invalid.");
        }
        validateCourseSessionType(course, type);
        session.setSessionType(type);

        String day = normalize(request.getDay()).toUpperCase();
        if (!VALID_DAYS.contains(day)) {
            throw new RuntimeException("Day is invalid.");
        }
        session.setDay(day);

        String startTime = normalize(request.getStartTime());
        String endTime = normalize(request.getEndTime());
        validateTime(startTime, "Start time is invalid. Use HH:mm or HH:mm:ss.");
        validateTime(endTime, "End time is invalid. Use HH:mm or HH:mm:ss.");

        Time start = Time.valueOf(normalizeSqlTime(startTime));
        Time end = Time.valueOf(normalizeSqlTime(endTime));
        if (!end.after(start)) {
            throw new RuntimeException("End time must be after start time.");
        }

        session.setStartTime(start.toString());
        session.setEndTime(end.toString());

        String venue = normalize(request.getVenue());
        if (venue.isEmpty()) {
            throw new RuntimeException("Venue is required.");
        }
        session.setVenue(venue);

        if (requireId && session.getId() <= 0) {
            throw new RuntimeException("Invalid timetable session ID.");
        }

        return session;
    }

    private void validateCourseSessionType(Course course, String sessionType) {
        String courseSessionType = normalize(course.getSessionType()).toUpperCase();

        if ("THEORY".equals(courseSessionType) && !"THEORY".equals(sessionType)) {
            throw new RuntimeException("This course allows only THEORY timetable sessions.");
        }

        if ("PRACTICAL".equals(courseSessionType) && !"PRACTICAL".equals(sessionType)) {
            throw new RuntimeException("This course allows only PRACTICAL timetable sessions.");
        }

        if ("BOTH".equals(courseSessionType)) {
            return;
        }
    }

    private void validateTime(String value, String message) {
        try {
            Time.valueOf(normalizeSqlTime(value));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(message);
        }
    }

    private String normalizeSqlTime(String value) {
        String normalized = normalize(value);
        if (normalized.matches("\\d{2}:\\d{2}")) {
            return normalized + ":00";
        }
        return normalized;
    }

    private int parsePositiveInt(String value, String message) {
        try {
            int parsed = Integer.parseInt(normalize(value));
            if (parsed <= 0) {
                throw new RuntimeException(message);
            }
            return parsed;
        } catch (NumberFormatException e) {
            throw new RuntimeException(message);
        }
    }

    private Integer parseOptionalPositiveInt(String value, String message) {
        String normalized = normalize(value);
        if (normalized.isEmpty()) {
            return null;
        }
        try {
            int parsed = Integer.parseInt(normalized);
            if (parsed <= 0) {
                throw new RuntimeException(message);
            }
            return parsed;
        } catch (NumberFormatException e) {
            throw new RuntimeException(message);
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
