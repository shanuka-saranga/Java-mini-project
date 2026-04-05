package com.fot.system.service;

import com.fot.system.model.TimetableEntry;

import java.util.*;

public class TimetableService {

    public Map<String, List<TimetableEntry>> getDummyTimetableData() {
        Map<String, List<TimetableEntry>> timetableData = new HashMap<>();

        timetableData.put("Monday", Arrays.asList(
                new TimetableEntry("Monday", "8:00 AM - 10:00 AM", "Object Oriented Programming", "Lab 02", "Mr. Kasun"),
                new TimetableEntry("Monday", "10:30 AM - 12:30 PM", "Database Management Systems", "Hall A", "Ms. Amara"),
                new TimetableEntry("Monday", "1:30 PM - 3:30 PM", "English II", "Room 204", "Mrs. Nimalka")
        ));

        timetableData.put("Tuesday", Arrays.asList(
                new TimetableEntry("Tuesday", "8:00 AM - 10:00 AM", "Data Structures and Algorithms", "Hall B", "Mr. Ruwan"),
                new TimetableEntry("Tuesday", "10:30 AM - 12:30 PM", "Object Oriented Analysis and Design", "Room 301", "Mr. Kamal")
        ));

        timetableData.put("Wednesday", Arrays.asList(
                new TimetableEntry("Wednesday", "9:00 AM - 11:00 AM", "E-Commerce Implementation", "Room 105", "Ms. Dewni"),
                new TimetableEntry("Wednesday", "1:00 PM - 3:00 PM", "Soft Skills", "Seminar Hall", "Mr. Sajith")
        ));

        timetableData.put("Thursday", Arrays.asList(
                new TimetableEntry("Thursday", "8:30 AM - 10:30 AM", "Business Economics", "Room 210", "Mrs. Piyali"),
                new TimetableEntry("Thursday", "11:00 AM - 1:00 PM", "Fundamentals of Management", "Hall C", "Mr. Bandula"),
                new TimetableEntry("Thursday", "2:00 PM - 4:00 PM", "OOP Practicum", "Lab 01", "Mr. Kasun")
        ));

        timetableData.put("Friday", Arrays.asList(
                new TimetableEntry("Friday", "8:00 AM - 10:00 AM", "Database Management Systems", "Lab 03", "Ms. Amara"),
                new TimetableEntry("Friday", "10:30 AM - 12:30 PM", "English II", "Room 204", "Mrs. Nimalka")
        ));

        return timetableData;
    }

    public Map<String, List<String>> getDummyNoticeData() {
        Map<String, List<String>> noticeData = new HashMap<>();

        noticeData.put("Monday", Arrays.asList(
                "Extra class for Database Management Systems at 3:45 PM in Lab 01.",
                "Bring lab record books for the OOP practical session."
        ));

        noticeData.put("Tuesday", Collections.singletonList(
                "Data Structures lecture venue changed from Hall B to Hall D."
        ));

        noticeData.put("Wednesday", Collections.singletonList(
                "Soft Skills presentation submission deadline is today before 5:00 PM."
        ));

        noticeData.put("Thursday", Arrays.asList(
                "OOP Practicum starts 15 minutes earlier than usual.",
                "Management lecture quiz will be held during the second hour."
        ));

        noticeData.put("Friday", Collections.singletonList(
                "English II class will end early due to faculty meeting."
        ));

        return noticeData;
    }
}