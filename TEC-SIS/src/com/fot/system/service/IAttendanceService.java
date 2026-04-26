package com.fot.system.service;

import com.fot.system.model.dto.*;

import java.util.List;

/**
 * define attendance service operations used by lecturer, student and TO flows
 * @author poornika
 */
public interface IAttendanceService {
    /**
     * load attendance rows for a course
     * @param courseId course id
     * @author poornika
     */
    List<AttendanceTableRow> getAttendanceRowsByCourse(int courseId);

    /**
     * load attendance sessions owned by lecturer
     * @param lecturerId lecturer user id
     * @author poornika
     */
    List<AttendanceSessionRow> getAttendanceSessionsByLecturer(int lecturerId);

    /**
     * load all attendance sessions
     * @author poornika
     */
    List<AttendanceSessionRow> getAllAttendanceSessions();

    /**
     * load editor data for a selected session
     * @param sessionId session id
     * @author poornika
     */
    AttendanceSessionEditorData getSessionEditorData(int sessionId);

    /**
     * create session for lecturer flow
     * @param request session create payload
     * @param lecturerId lecturer user id
     * @author poornika
     */
    AttendanceSessionRow addSession(AddAttendanceSessionRequest request, int lecturerId);

    /**
     * create session for technical officer flow
     * @param request session create payload
     * @author methum
     */
    AttendanceSessionRow addSessionForTo(AddAttendanceSessionRequest request);

    /**
     * save attendance marks for a session
     * @param sessionId session id
     * @param markedBy marker user id
     * @param updates student attendance updates
     * @author poornika
     */
    void saveSessionAttendance(int sessionId, int markedBy, List<StudentAttendanceUpdate> updates);

    /**
     * build lecturer attendance page data
     * @param courseId course id
     * @param totalCourseHours course total hours
     * @author poornika
     */
    CourseAttendanceViewData getCourseAttendanceViewData(int courseId, int totalCourseHours);

    /**
     * build student attendance and medical page data
     * @param studentUserId student user id
     * @author poornika
     */
    StudentAttendanceMedicalViewData getStudentAttendanceMedicalViewData(int studentUserId);

    /**
     * count pending medical submissions
     * @author methum
     */
    int getPendingMedicalSubmissionCount();
}
