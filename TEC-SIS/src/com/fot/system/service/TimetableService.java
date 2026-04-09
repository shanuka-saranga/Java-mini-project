package com.fot.system.service;

import com.fot.system.model.TimetableSession;
import com.fot.system.repository.TimetableRepository;

import java.util.List;

public class TimetableService {

    private final TimetableRepository timetableRepository;

    public TimetableService() {
        this.timetableRepository = new TimetableRepository();
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
}
