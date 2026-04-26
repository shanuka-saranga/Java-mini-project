package com.fot.system.service;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.repository.AttendanceRepository;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * Handles attendance business logic for attendance management, eligibility, and medical flows.
 * @author methum
 */
public class AttendanceService implements IAttendanceService {
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String STATUS_PRESENT = "PRESENT";
    private static final String STATUS_MEDICAL = "MEDICAL";
    private static final String STATUS_ABSENT = "ABSENT";
    private static final double ELIGIBILITY_THRESHOLD_PERCENT = 79.0;

    private final AttendanceRepository attendanceRepository;

    /**
     * Initializes attendance service dependencies.
     * @author methum
     */
    public AttendanceService() {
        this.attendanceRepository = new AttendanceRepository();
    }

    /**
     * load attendance rows by course id
     * @param courseId course id
     * @author poornika
     */
    public List<AttendanceTableRow> getAttendanceRowsByCourse(int courseId) {
        if (courseId <= 0) {
            throw new RuntimeException("Invalid course ID.");
        }
        return attendanceRepository.findAttendanceRowsByCourse(courseId);
    }

    /**
     * load attendance sessions by lecturer id
     * @param lecturerId lecturer user id
     * @author poornika
     */
    public List<AttendanceSessionRow> getAttendanceSessionsByLecturer(int lecturerId) {
        if (lecturerId <= 0) {
            throw new RuntimeException("Invalid lecturer ID.");
        }
        return attendanceRepository.findAttendanceSessionsByLecturer(lecturerId);
    }

    /**
     * Loads all attendance sessions.
     * @author methum
     */
    public List<AttendanceSessionRow> getAllAttendanceSessions() {
        return attendanceRepository.findAllAttendanceSessions();
    }

    /**
     * Loads the attendance editor dataset for one session.
     * @param sessionId session id
     * @author methum
     */
    public AttendanceSessionEditorData getSessionEditorData(int sessionId) {
        if (sessionId <= 0) {
            throw new RuntimeException("Invalid session ID.");
        }
        return buildSessionEditorData(sessionId);
    }

    /**
     * Creates an attendance session for the lecturer flow.
     * @param request add session payload
     * @param lecturerId lecturer user id
     * @author methum
     */
    public AttendanceSessionRow addSession(AddAttendanceSessionRequest request, int lecturerId) {
        requireSessionRequest(request);

        int courseId = parsePositiveInt(request.getCourseId(), "Course is required.");
        int timetableSessionId = parsePositiveInt(request.getTimetableSessionId(), "Timetable session is required.");
        LocalDate sessionDate = parseDate(request.getSessionDate(), "Session date is required.");
        if (lecturerId <= 0) {
            throw new RuntimeException("Invalid lecturer.");
        }

        return attendanceRepository.createSessionFromTimetable(lecturerId, courseId, timetableSessionId, sessionDate);
    }

    /**
     * create attendance session for technical officer flow
     * @param request add session payload
     * @author methum
     */
    public AttendanceSessionRow addSessionForTo(AddAttendanceSessionRequest request) {
        requireSessionRequest(request);

        int courseId = parsePositiveInt(request.getCourseId(), "Course is required.");
        int timetableSessionId = parsePositiveInt(request.getTimetableSessionId(), "Timetable session is required.");
        LocalDate sessionDate = parseDate(request.getSessionDate(), "Session date is required.");
        return attendanceRepository.createSessionFromTimetable(courseId, timetableSessionId, sessionDate);
    }

    /**
     * Saves attendance statuses for a session.
     * @param sessionId session id
     * @param markedBy marker user id
     * @param updates updates list
     * @author methum
     */
    public void saveSessionAttendance(int sessionId, int markedBy, List<StudentAttendanceUpdate> updates) {
        if (sessionId <= 0) {
            throw new RuntimeException("Invalid session.");
        }
        if (markedBy <= 0) {
            throw new RuntimeException("Invalid marker.");
        }
        validateAttendanceUpdates(updates);
        attendanceRepository.saveSessionAttendance(sessionId, markedBy, updates);
    }

    /**
     * build lecturer attendance table, progress and summary data
     * @param courseId course id
     * @param totalCourseHours total configured course hours
     * @author poornika
     */
    public CourseAttendanceViewData getCourseAttendanceViewData(int courseId, int totalCourseHours) {
        if (courseId <= 0) {
            throw new RuntimeException("Invalid course ID.");
        }
        if (totalCourseHours < 0) {
            throw new RuntimeException("Invalid total course hours.");
        }

        List<AttendanceTableRow> attendanceRows = attendanceRepository.findAttendanceRowsByCourse(courseId);
        CourseAttendanceViewData viewData = new CourseAttendanceViewData();
        viewData.setAttendanceRows(attendanceRows);
        viewData.setCourseProgress(buildCourseProgress(attendanceRows, totalCourseHours));
        viewData.setStudentSummaryRows(buildStudentSummaryRows(attendanceRows));
        return viewData;
    }

    /**
     * build student attendance and medical page data
     * @param studentUserId student user id
     * @author poornika
     */
    public StudentAttendanceMedicalViewData getStudentAttendanceMedicalViewData(int studentUserId) {
        if (studentUserId <= 0) {
            throw new RuntimeException("Invalid student user ID.");
        }

        List<StudentSessionAttendanceRow> attendanceRows = attendanceRepository.findStudentSessionAttendanceRows(studentUserId);
        List<StudentMedicalRow> medicalRows = attendanceRepository.findStudentMedicalRows(studentUserId);

        StudentAttendanceMedicalViewData viewData = new StudentAttendanceMedicalViewData();
        viewData.setAttendanceRows(attendanceRows);
        viewData.setMedicalRows(medicalRows);
        return viewData;
    }

    /**
     * count pending medical submissions
     * @author methum
     */
    public int getPendingMedicalSubmissionCount() {
        return attendanceRepository.countPendingMedicalSubmissions();
    }

    /**
     * Builds the attendance editor data bundle for a single session.
     * @param sessionId session id
     * @author methum
     */
    private AttendanceSessionEditorData buildSessionEditorData(int sessionId) {
        AttendanceSessionEditorData data = new AttendanceSessionEditorData();
        data.setSession(attendanceRepository.findAttendanceSessionById(sessionId));
        data.setStudentRows(attendanceRepository.findStudentAttendanceRowsBySession(sessionId));
        return data;
    }

    /**
     * Validates the add-session request object.
     * @param request add-session request
     * @author methum
     */
    private void requireSessionRequest(AddAttendanceSessionRequest request) {
        if (request == null) {
            throw new RuntimeException("Attendance session request is required.");
        }
    }

    /**
     * Validates the attendance update list.
     * @param updates attendance updates
     * @author methum
     */
    private void validateAttendanceUpdates(List<StudentAttendanceUpdate> updates) {
        if (updates == null || updates.isEmpty()) {
            throw new RuntimeException("Attendance updates are required.");
        }
    }

    /**
     * calculate held hours and progress percentage from completed sessions
     * @param attendanceRows attendance rows
     * @param totalCourseHours configured course hours
     * @author poornika
     */
    private AttendanceCourseProgress buildCourseProgress(List<AttendanceTableRow> attendanceRows, int totalCourseHours) {
        Set<String> completedSessions = new HashSet<>();
        for (AttendanceTableRow row : attendanceRows) {
            if (equalsIgnoreCase(row.getSessionStatus(), STATUS_COMPLETED)) {
                completedSessions.add(row.getSessionType() + "|" + row.getSessionNo() + "|" + row.getSessionDate() + "|" + row.getTimeRange());
            }
        }

        int heldHours = completedSessions.size() * 2;
        double progressPercentage = totalCourseHours <= 0
                ? 0
                : Math.min(100.0, (heldHours * 100.0) / totalCourseHours);

        AttendanceCourseProgress progress = new AttendanceCourseProgress();
        progress.setHeldHours(heldHours);
        progress.setTotalHours(totalCourseHours);
        progress.setProgressPercentage(progressPercentage);
        return progress;
    }

    /**
     * aggregate per-student attendance summary rows
     * @param attendanceRows attendance rows
     * @author poornika
     */
    private List<StudentAttendanceSummaryRow> buildStudentSummaryRows(List<AttendanceTableRow> attendanceRows) {
        Map<String, StudentAttendanceAccumulator> accumulators = new LinkedHashMap<>();

        for (AttendanceTableRow row : attendanceRows) {
            StudentAttendanceAccumulator accumulator = accumulators.computeIfAbsent(
                    row.getRegistrationNo(),
                    key -> new StudentAttendanceAccumulator(row.getRegistrationNo(), row.getStudentName())
            );

            if (!equalsIgnoreCase(row.getSessionStatus(), STATUS_COMPLETED)) {
                continue;
            }

            accumulator.totalHeldSessions++;
            if (equalsIgnoreCase(row.getAttendanceStatus(), STATUS_PRESENT)) {
                accumulator.presentCount++;
            } else if (equalsIgnoreCase(row.getAttendanceStatus(), STATUS_MEDICAL)) {
                accumulator.medicalCount++;
            } else if (equalsIgnoreCase(row.getAttendanceStatus(), STATUS_ABSENT)) {
                accumulator.absentCount++;
            }
        }

        List<StudentAttendanceSummaryRow> summaryRows = new ArrayList<>();
        for (StudentAttendanceAccumulator accumulator : accumulators.values()) {
            double attendancePercentage = accumulator.totalHeldSessions == 0
                    ? 0
                    : ((accumulator.presentCount + accumulator.medicalCount) * 100.0) / accumulator.totalHeldSessions;

            StudentAttendanceSummaryRow row = new StudentAttendanceSummaryRow();
            row.setRegistrationNo(accumulator.registrationNo);
            row.setStudentName(accumulator.studentName);
            row.setPresentCount(accumulator.presentCount);
            row.setMedicalCount(accumulator.medicalCount);
            row.setAbsentCount(accumulator.absentCount);
            row.setAttendancePercentage(attendancePercentage);
            row.setEligible(attendancePercentage > ELIGIBILITY_THRESHOLD_PERCENT);
            summaryRows.add(row);
        }

        return summaryRows;
    }

    /**
     * helper accumulator used when computing student summary rows
     * @author poornika
     */
    private static class StudentAttendanceAccumulator {
        private final String registrationNo;
        private final String studentName;
        private int totalHeldSessions;
        private int presentCount;
        private int medicalCount;
        private int absentCount;

        private StudentAttendanceAccumulator(String registrationNo, String studentName) {
            this.registrationNo = registrationNo;
            this.studentName = studentName;
        }
    }

    /**
     * parse a positive integer with validation
     * @param value string value
     * @param message validation message
     * @author poornika
     */
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

    /**
     * parse local date using YYYY-MM-DD format
     * @param value date value
     * @param message validation message
     * @author poornika
     */
    private LocalDate parseDate(String value, String message) {
        try {
            return LocalDate.parse(normalize(value));
        } catch (Exception e) {
            throw new RuntimeException(message + " Use YYYY-MM-DD.");
        }
    }

    /**
     * normalize nullable string values
     * @param value raw string
     * @author poornika
     */
    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    /**
     * compare strings ignoring case with null safety
     * @param left first value
     * @param right second value
     * @author poornika
     */
    private boolean equalsIgnoreCase(String left, String right) {
        return normalize(left).equalsIgnoreCase(normalize(right));
    }
}
