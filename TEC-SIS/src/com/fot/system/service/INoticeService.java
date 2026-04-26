package com.fot.system.service;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;

import java.util.List;

/**
 * define notice service operations used by admin and shared views
 * @author janith
 */
public interface INoticeService {
    /**
     * get all notices
     * @author janith
     */
    List<Notice> getAllNotices();

    /**
     * get active notice count
     * @author janith
     */
    int getActiveNoticeCount();

    /**
     * get visible notice count for a role
     * @param role user role
     * @author janith
     */
    int getVisibleNoticeCountForRole(String role);

    /**
     * get recent visible notices for a role
     * @param role user role
     * @param limit max result count
     * @author janith
     */
    List<Notice> getRecentVisibleNoticesForRole(String role, int limit);

    /**
     * get notice by id
     * @param noticeId notice id
     * @author janith
     */
    Notice getNoticeById(int noticeId);

    /**
     * add notice
     * @param request add notice payload
     * @author janith
     */
    Notice addNotice(AddNoticeRequest request);

    /**
     * update notice
     * @param request edit notice payload
     * @author janith
     */
    Notice updateNotice(EditNoticeRequest request);

    /**
     * delete notice by id
     * @param noticeId notice id
     * @author janith
     */
    void deleteNotice(int noticeId);
}
