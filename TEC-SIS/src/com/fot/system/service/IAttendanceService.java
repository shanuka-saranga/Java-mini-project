package com.fot.system.service;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;

import java.util.List;

public interface IAttendanceService {
    List<AttendanceTableRow> getAttendanceRowsByCourse(int courseId);
    List<AttendanceSessionRow> getAttendanceSessionsByLecturer(int lecturerId);
    List<AttendanceSessionRow> getAllAttendanceSessions();
    AttendanceSessionEditorData getSessionEditorData(int sessionId);
    AttendanceSessionRow addSession(AddAttendanceSessionRequest request, int lecturerId);
    AttendanceSessionRow addSessionForTo(AddAttendanceSessionRequest request);
    void saveSessionAttendance(int sessionId, int markedBy, List<StudentAttendanceUpdate> updates);
    CourseAttendanceViewData getCourseAttendanceViewData(int courseId, int totalCourseHours);
    StudentAttendanceMedicalViewData getStudentAttendanceMedicalViewData(int studentUserId);
    int getPendingMedicalSubmissionCount();
}
