package com.fot.system.model.dto;

import com.fot.system.model.entity.Notice;

import java.util.List;

public class ToDashboardData {
    private final int pendingMedicals;
    private final int visibleNotices;
    private final List<Notice> notices;

    public ToDashboardData(int pendingMedicals, int visibleNotices, List<Notice> notices) {
        this.pendingMedicals = pendingMedicals;
        this.visibleNotices = visibleNotices;
        this.notices = notices;
    }

    public int getPendingMedicals() {
        return pendingMedicals;
    }

    public int getVisibleNotices() {
        return visibleNotices;
    }

    public List<Notice> getNotices() {
        return notices;
    }
}
