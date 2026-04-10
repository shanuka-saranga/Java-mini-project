package com.fot.system.service;

import com.fot.system.repository.AttendanceRepository;

import java.util.List;

/**
 * AttendanceService - Business Logic for attendance.
 * Handles 80% eligibility check, summary generation.
 * Demonstrates: Abstraction, Error & Exception Handling
 */
public class AttendanceService {

    private static final double ELIGIBILITY_THRESHOLD = 80.0;

    private final AttendanceRepository repo;

    public AttendanceService() {
        this.repo = new AttendanceRepository();
    }

    /**
     * Add a new attendance record.
     * Throws exception if record already exists for that student + lecture.
     */
    public boolean addAttendance(Attendance attendance) {
        if (attendance.getRegNo() == null || attendance.getRegNo().isEmpty()) {
            throw new IllegalArgumentException("Student registration number cannot be empty.");
        }
        if (attendance.getStatus() == null ||
                (!attendance.getStatus().equals("Present") && !attendance.getStatus().equals("Absent"))) {
            throw new IllegalArgumentException("Status must be 'Present' or 'Absent'.");
        }
        if (repo.exists(attendance.getRegNo(), attendance.getLectureId())) {
            throw new IllegalStateException("Attendance already recorded for this student and session.");
        }
        return repo.save(attendance);
    }

    /**
     * Update an attendance record.
     */
    public boolean updateAttendance(Attendance attendance) {
        if (attendance.getAttendanceId() <= 0) {
            throw new IllegalArgumentException("Invalid attendance ID.");
        }
        return repo.update(attendance);
    }

    /**
     * Delete an attendance record.
     */
    public boolean deleteAttendance(int attendanceId) {
        return repo.delete(attendanceId);
    }

    /**
     * Get all attendance records for a student.
     */
    public List<Attendance> getAttendanceForStudent(String regNo) {
        return repo.findByStudent(regNo);
    }

    /**
     * Get all attendance for a lecture session.
     */
    public List<Attendance> getAttendanceForLecture(int lectureId) {
        return repo.findByLecture(lectureId);
    }

    /**
     * Calculate attendance percentage for a student in one course + session type.
     *
     * @param totalSessions total number of sessions held
     * @param presentCount  number of sessions the student attended
     * @return percentage (0.0 – 100.0)
     */
    public double calculatePercentage(int totalSessions, int presentCount) {
        if (totalSessions == 0) return 0.0;
        return ((double) presentCount / totalSessions) * 100.0;
    }

    /**
     * Check if a student is eligible (attendance >= 80%).
     */
    public boolean isEligible(int totalSessions, int presentCount) {
        return calculatePercentage(totalSessions, presentCount) >= ELIGIBILITY_THRESHOLD;
    }

    /**
     * Get formatted summary rows for a student's attendance across all courses.
     * Each row: [courseCode, courseName, sessionType, total, present, percentage, eligible]
     */
    public List<Object[]> getStudentSummary(String regNo) {
        List<Object[]> raw = repo.getAttendanceSummaryByStudent(regNo);
        for (Object[] row : raw) {
            int total   = (int) row[3];
            int present = (int) row[4];
            double pct  = calculatePercentage(total, present);
            // Append percentage and eligibility to each row
            row = java.util.Arrays.copyOf(row, 7);
            row[5] = String.format("%.1f%%", pct);
            row[6] = pct >= ELIGIBILITY_THRESHOLD ? "Eligible" : "Not Eligible";
        }
        return raw;
    }

    /**
     * Get formatted summary rows for all students (whole batch).
     * Each row: [regNo, studentName, courseCode, courseName, sessionType,
     *            total, present, percentage, eligible]
     */
    public List<Object[]> getBatchSummary() {
        List<Object[]> raw = repo.getAttendanceSummaryAllStudents();
        for (int i = 0; i < raw.size(); i++) {
            Object[] row = raw.get(i);
            int total   = (int) row[5];
            int present = (int) row[6];
            double pct  = calculatePercentage(total, present);
            Object[] extended = java.util.Arrays.copyOf(row, 9);
            extended[7] = String.format("%.1f%%", pct);
            extended[8] = pct >= ELIGIBILITY_THRESHOLD ? "Eligible" : "Not Eligible";
            raw.set(i, extended);
        }
        return raw;
    }
}
