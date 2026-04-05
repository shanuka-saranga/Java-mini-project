package com.fot.system.view.dashboard.student;

import com.fot.system.model.TimetableEntry;
import com.fot.system.service.TimetableService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class MyTimetablePanel extends JPanel {

    private final Color bgColor = new Color(245, 247, 250);

    private final TimetableService timetableService;
    private final Map<String, java.util.List<TimetableEntry>> timetableData;
    private final Map<String, java.util.List<String>> noticeData;

    private TimetableDayBar dayBar;
    private TimetableTablePanel tablePanel;
    private TimetableNoticePanel noticePanel;

    private String currentDay = "Monday";

    public MyTimetablePanel() {
        this.timetableService = new TimetableService();
        this.timetableData = timetableService.getDummyTimetableData();
        this.noticeData = timetableService.getDummyNoticeData();

        setLayout(new BorderLayout());
        setBackground(bgColor);

        initializeUI();
        loadDayData(currentDay);
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel titleLabel = new JLabel("My Timetable");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(bgColor);
        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(15));

        dayBar = new TimetableDayBar(selectedDay -> {
            currentDay = selectedDay;
            loadDayData(currentDay);
        });
        topPanel.add(dayBar);

        tablePanel = new TimetableTablePanel();
        noticePanel = new TimetableNoticePanel();

        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setBackground(bgColor);
        contentPanel.add(tablePanel);
        contentPanel.add(noticePanel);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void loadDayData(String day) {
        List<TimetableEntry> entries = timetableData.getOrDefault(day, java.util.Collections.emptyList());
        List<String> notices = noticeData.getOrDefault(day, java.util.Collections.emptyList());

        tablePanel.setDaySchedule(day, entries);
        noticePanel.setNotices(day, notices);
        dayBar.setSelectedDay(day);
    }
}