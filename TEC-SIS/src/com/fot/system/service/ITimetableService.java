package com.fot.system.service;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;

import java.util.List;

public interface ITimetableService {
    List<TimetableSession> getAllTimetableSessions();
    List<TimetableSession> getTimetableByLecturer(int lecturerId);
    TimetableSession getSessionById(int sessionId);
    TimetableSession addSession(TimetableSessionRequest request);
    TimetableSession updateSession(TimetableSessionRequest request);
    void deleteSession(int sessionId);
}
