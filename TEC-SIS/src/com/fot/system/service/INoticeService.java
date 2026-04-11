package com.fot.system.service;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;

import java.util.List;

public interface INoticeService {
    List<Notice> getAllNotices();
    int getActiveNoticeCount();
    int getVisibleNoticeCountForRole(String role);
    List<Notice> getRecentVisibleNoticesForRole(String role, int limit);
    Notice getNoticeById(int noticeId);
    Notice addNotice(AddNoticeRequest request);
    Notice updateNotice(EditNoticeRequest request);
    void deleteNotice(int noticeId);
}
