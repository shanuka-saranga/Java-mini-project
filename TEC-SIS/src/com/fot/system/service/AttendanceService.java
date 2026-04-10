package com.fot.system.service;

import com.fot.system.model.AttendanceCourseProgress;
import com.fot.system.model.AttendanceSessionEditorData;
import com.fot.system.model.AttendanceSessionRow;
import com.fot.system.model.AttendanceTableRow;
import com.fot.system.model.AddAttendanceSessionRequest;
import com.fot.system.model.CourseAttendanceViewData;
import com.fot.system.model.StudentAttendanceMedicalViewData;
import com.fot.system.model.StudentAttendanceEditRow;
import com.fot.system.model.StudentMedicalRow;
import com.fot.system.model.StudentSessionAttendanceRow;
import com.fot.system.model.StudentAttendanceSummaryRow;
import com.fot.system.model.StudentAttendanceUpdate;
import com.fot.system.repository.AttendanceRepository;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    public AttendanceService() {
        this.attendanceRepository = new AttendanceRepository();
    }

    public List<AttendanceTableRow> getAttendanceRowsByCourse(int courseId) {
        if (courseId <= 0) {
            throw new RuntimeException("Invalid course ID.");
        }
        return attendanceRepository.findAttendanceRowsByCourse(courseId);
    }

    public List<AttendanceSessionRow> getAttendanceSessionsByLecturer(int lecturerId) {
        if (lecturerId <= 0) {
            throw new RuntimeException("Invalid lecturer ID.");
        }
        return attendanceRepository.findAttendanceSessionsByLecturer(lecturerId);
    }

    public List<AttendanceSessionRow> getAllAttendanceSessions() {
        return attendanceRepository.findAllAttendanceSessions();
    }

    public AttendanceSessionEditorData getSessionEditorData(int sessionId) {
        if (sessionId <= 0) {
            throw new RuntimeException("Invalid session ID.");
        }

        AttendanceSessionEditorData data = new AttendanceSessionEditorData();
        data.setSession(attendanceRepository.findAttendanceSessionById(sessionId));
        data.setStudentRows(attendanceRepository.findStudentAttendanceRowsBySession(sessionId));
        return data;
    }

    public AttendanceSessionRow addSession(AddAttendanceSessionRequest request, int lecturerId) {
        if (request == null) {
            throw new RuntimeException("Attendance session request is required.");
        }

        int courseId = parsePositiveInt(request.getCourseId(), "Course is required.");
        int timetableSessionId = parsePositiveInt(request.getTimetableSessionId(), "Timetable session is required.");
        LocalDate sessionDate = parseDate(request.getSessionDate(), "Session date is required.");
        if (lecturerId <= 0) {
            throw new RuntimeException("Invalid lecturer.");
        }

        return attendanceRepository.createSessionFromTimetable(lecturerId, courseId, timetableSessionId, sessionDate);
    }

    public AttendanceSessionRow addSessionForTo(AddAttendanceSessionRequest request) {
        if (request == null) {
            throw new RuntimeException("Attendance session request is required.");
        }

        int courseId = parsePositiveInt(request.getCourseId(), "Course is required.");
        int timetableSessionId = parsePositiveInt(request.getTimetableSessionId(), "Timetable session is required.");
        LocalDate sessionDate = parseDate(request.getSessionDate(), "Session date is required.");
        return attendanceRepository.createSessionFromTimetable(courseId, timetableSessionId, sessionDate);
    }

    public void saveSessionAttendance(int sessionId, int markedBy, List<StudentAttendanceUpdate> updates) {
        if (sessionId <= 0) {
            throw new RuntimeException("Invalid session.");
        }
        if (markedBy <= 0) {
            throw new RuntimeException("Invalid marker.");
        }
        if (updates == null || updates.isEmpty()) {
            throw new RuntimeException("Attendance updates are required.");
        }
        attendanceRepository.saveSessionAttendance(sessionId, markedBy, updates);
    }

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

    public int getPendingMedicalSubmissionCount() {
        return attendanceRepository.countPendingMedicalSubmissions();
    }

    private AttendanceCourseProgress buildCourseProgress(List<AttendanceTableRow> attendanceRows, int totalCourseHours) {
        Set<String> completedSessions = new HashSet<>();
        for (AttendanceTableRow row : attendanceRows) {
            if ("COMPLETED".equalsIgnoreCase(row.getSessionStatus())) {
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

    private List<StudentAttendanceSummaryRow> buildStudentSummaryRows(List<AttendanceTableRow> attendanceRows) {
        Map<String, StudentAttendanceAccumulator> accumulators = new LinkedHashMap<>();

        for (AttendanceTableRow row : attendanceRows) {
            StudentAttendanceAccumulator accumulator = accumulators.computeIfAbsent(
                    row.getRegistrationNo(),
                    key -> new StudentAttendanceAccumulator(row.getRegistrationNo(), row.getStudentName())
            );

            if (!"COMPLETED".equalsIgnoreCase(row.getSessionStatus())) {
                continue;
            }

            accumulator.totalHeldSessions++;
            if ("PRESENT".equalsIgnoreCase(row.getAttendanceStatus())) {
                accumulator.presentCount++;
            } else if ("MEDICAL".equalsIgnoreCase(row.getAttendanceStatus())) {
                accumulator.medicalCount++;
                if ("APPROVED".equalsIgnoreCase(row.getMedicalApprovalStatus())) {
                    accumulator.approvedMedicalCount++;
                }
            } else if ("ABSENT".equalsIgnoreCase(row.getAttendanceStatus())) {
                accumulator.absentCount++;
            }
        }

        List<StudentAttendanceSummaryRow> summaryRows = new ArrayList<>();
        for (StudentAttendanceAccumulator accumulator : accumulators.values()) {
            double attendancePercentage = accumulator.totalHeldSessions == 0
                    ? 0
                    : ((accumulator.presentCount + accumulator.approvedMedicalCount) * 100.0) / accumulator.totalHeldSessions;

            StudentAttendanceSummaryRow row = new StudentAttendanceSummaryRow();
            row.setRegistrationNo(accumulator.registrationNo);
            row.setStudentName(accumulator.studentName);
            row.setPresentCount(accumulator.presentCount);
            row.setMedicalCount(accumulator.medicalCount);
            row.setAbsentCount(accumulator.absentCount);
            row.setAttendancePercentage(attendancePercentage);
            row.setEligible(attendancePercentage > 79.0);
            summaryRows.add(row);
        }

        return summaryRows;
    }

    private static class StudentAttendanceAccumulator {
        private final String registrationNo;
        private final String studentName;
        private int totalHeldSessions;
        private int presentCount;
        private int medicalCount;
        private int approvedMedicalCount;
        private int absentCount;

        private StudentAttendanceAccumulator(String registrationNo, String studentName) {
            this.registrationNo = registrationNo;
            this.studentName = studentName;
        }
    }

    private int parsePositiveInt(String value, String message) {
        try {
            int parsed = Integer.parseInt(value == null ? "" : value.trim());
            if (parsed <= 0) {
                throw new RuntimeException(message);
            }
            return parsed;
        } catch (NumberFormatException e) {
            throw new RuntimeException(message);
        }
    }

    private LocalDate parseDate(String value, String message) {
        try {
            return LocalDate.parse(value == null ? "" : value.trim());
        } catch (Exception e) {
            throw new RuntimeException(message + " Use YYYY-MM-DD.");
        }
    }
}
