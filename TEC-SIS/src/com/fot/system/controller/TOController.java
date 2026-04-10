package com.fot.system.controller;

import com.fot.system.repository.UserRepository;

import javax.swing.*;
import java.util.List;

/**
 * TOController - Event Handler / Controller for Technical Officer module.
 * Connects View (UI panels) with Service (business logic).
 * Demonstrates: Encapsulation, Error & Exception Handling
 */
public class TOController {

    private final AttendanceService attendanceService;
    private final MedicalService medicalService;
    private final UserRepository userRepository;
    private final TechnicalOfficer currentUser;

    public TOController(TechnicalOfficer currentUser) {
        this.currentUser = currentUser;
        this.attendanceService = new AttendanceService();
        this.medicalService = new MedicalService();
        this.userRepository = new UserRepository();
    }

    // ─────────────────────────────────────────────
    // ATTENDANCE
    // ─────────────────────────────────────────────

    public boolean addAttendance(String regNo, int lectureId, java.util.Date sessionDate, String status) {
        try {
            Attendance a = new Attendance();
            a.setRegNo(regNo);
            a.setLectureId(lectureId);
            a.setSessionDate(sessionDate);
            a.setStatus(status);
            a.setRecordedBy(currentUser.getId());
            return attendanceService.addAttendance(a);
        } catch (IllegalArgumentException | IllegalStateException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Unexpected error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean updateAttendance(int attendanceId, String newStatus) {
        try {
            Attendance a = new Attendance();
            a.setAttendanceId(attendanceId);
            a.setStatus(newStatus);
            return attendanceService.updateAttendance(a);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error updating attendance: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean deleteAttendance(int attendanceId) {
        int confirm = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to delete this attendance record?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            return attendanceService.deleteAttendance(attendanceId);
        }
        return false;
    }

    public List<Attendance> getAttendanceForStudent(String regNo) {
        return attendanceService.getAttendanceForStudent(regNo);
    }

    public List<Object[]> getStudentAttendanceSummary(String regNo) {
        return attendanceService.getStudentSummary(regNo);
    }

    public List<Object[]> getBatchAttendanceSummary() {
        return attendanceService.getBatchSummary();
    }

    // ─────────────────────────────────────────────
    // MEDICALS
    // ─────────────────────────────────────────────

    public boolean addMedical(Medical medical, List<Integer> lectureIds) {
        try {
            return medicalService.addMedical(medical, lectureIds);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Unexpected error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean approveMedical(int medicalId) {
        boolean result = medicalService.approveMedical(medicalId);
        if (result) {
            JOptionPane.showMessageDialog(null, "Medical approved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
        return result;
    }

    public boolean rejectMedical(int medicalId) {
        boolean result = medicalService.rejectMedical(medicalId);
        if (result) {
            JOptionPane.showMessageDialog(null, "Medical rejected.", "Done", JOptionPane.INFORMATION_MESSAGE);
        }
        return result;
    }

    public List<Medical> getPendingMedicals() {
        return medicalService.getPendingMedicals();
    }

    public List<Medical> getAllMedicals() {
        return medicalService.getAllMedicals();
    }

    public List<Medical> getMedicalsForStudent(String regNo) {
        return medicalService.getMedicalsForStudent(regNo);
    }

    // ─────────────────────────────────────────────
    // PROFILE
    // ─────────────────────────────────────────────

    public boolean updateProfile(String email, String phone, String address) {
        try {
            currentUser.setEmail(email);
            currentUser.setPhone(phone);
            currentUser.setAddress(address);
            boolean result = userRepository.updateUserProfile(currentUser);
            if (result) {
                JOptionPane.showMessageDialog(null, "Profile updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            return result;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error updating profile: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public TechnicalOfficer getCurrentUser() {
        return currentUser;
    }
}
