package com.fot.system.controller;

import com.fot.system.model.TimetableSession;
import com.fot.system.model.TimetableSessionRequest;
import com.fot.system.service.TimetableService;

public class TimetableSessionController {

    private final TimetableService timetableService;

    public TimetableSessionController() {
        this.timetableService = new TimetableService();
    }

    public TimetableSession createSession(TimetableSessionRequest request) {
        return timetableService.addSession(request);
    }

    public TimetableSession updateSession(TimetableSessionRequest request) {
        return timetableService.updateSession(request);
    }

    public void deleteSession(int sessionId) {
        timetableService.deleteSession(sessionId);
    }
}
