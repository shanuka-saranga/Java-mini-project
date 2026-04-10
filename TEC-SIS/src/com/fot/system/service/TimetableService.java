package com.fot.system.service;

import com.fot.system.model.TimetableEntry;
import com.fot.system.repository.NoticeRepository;
import com.fot.system.repository.TimetableRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimetableService {
    private final TimetableRepository timetableRepository;
    private final NoticeRepository noticeRepository;

    public TimetableService() {
        this.timetableRepository = new TimetableRepository();
        this.noticeRepository = new NoticeRepository();
    }

    public Map<String, List<TimetableEntry>> getDummyTimetableData() {
        return timetableRepository.findWeeklyTimetable();
    }

    public Map<String, List<String>> getDummyNoticeData() {
        List<String> activeStudentNotices = noticeRepository.findActiveNoticeMessagesForAudience("STUDENT");
        Map<String, List<String>> noticeData = new HashMap<>();
        noticeData.put("Monday", new ArrayList<>(activeStudentNotices));
        noticeData.put("Tuesday", new ArrayList<>(activeStudentNotices));
        noticeData.put("Wednesday", new ArrayList<>(activeStudentNotices));
        noticeData.put("Thursday", new ArrayList<>(activeStudentNotices));
        noticeData.put("Friday", new ArrayList<>(activeStudentNotices));
        return noticeData;
    }
}
