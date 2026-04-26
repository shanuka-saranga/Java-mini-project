package com.fot.system.service;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.repository.NoticeRepository;

import java.sql.Date;
import java.util.List;
import java.util.Set;

/**
 * manage notice business logic and validation
 * @author janith
 */
public class NoticeService implements INoticeService {

    private static final Set<String> VALID_AUDIENCE = Set.of("ALL", "STUDENT", "LECTURER", "TO");
    private static final Set<String> VALID_PRIORITY = Set.of("LOW", "MEDIUM", "HIGH");
    private static final Set<String> VALID_STATUS = Set.of("ACTIVE", "INACTIVE");

    private final NoticeRepository noticeRepository;

    /**
     * initialize notice service dependencies
     * @author janith
     */
    public NoticeService() {
        this.noticeRepository = new NoticeRepository();
    }

    /**
     * get all notices
     * @author janith
     */
    public List<Notice> getAllNotices() {
        return noticeRepository.findAll();
    }

    /**
     * get active notice count
     * @author janith
     */
    public int getActiveNoticeCount() {
        return noticeRepository.countActive();
    }

    /**
     * get visible notice count for given role
     * @param role target role
     * @author janith
     */
    public int getVisibleNoticeCountForRole(String role) {
        return noticeRepository.countVisibleByRole(role);
    }

    /**
     * get recent visible notices for given role
     * @param role target role
     * @param limit requested maximum count
     * @author janith
     */
    public List<Notice> getRecentVisibleNoticesForRole(String role, int limit) {
        int safeLimit = limit <= 0 ? 5 : limit;
        return noticeRepository.findRecentVisibleByRole(role, safeLimit);
    }

    /**
     * get notice by id
     * @param noticeId notice id
     * @author janith
     */
    public Notice getNoticeById(int noticeId) {
        if (noticeId <= 0) {
            throw new RuntimeException("Invalid notice ID.");
        }
        return noticeRepository.findById(noticeId);
    }

    /**
     * add notice after validation
     * @param request add notice request payload
     * @author janith
     */
    public Notice addNotice(AddNoticeRequest request) {
        Notice notice = validate(createNotice(request), false);
        if (!noticeRepository.save(notice)) {
            throw new RuntimeException("Notice save failed.");
        }
        return noticeRepository.findById(notice.getId());
    }

    /**
     * update notice after validation
     * @param request edit notice request payload
     * @author janith
     */
    public Notice updateNotice(EditNoticeRequest request) {
        Notice notice = createNotice(request);
        notice.setId(request.getNoticeId());
        Notice validatedNotice = validate(notice, true);
        if (!noticeRepository.update(validatedNotice)) {
            throw new RuntimeException("Notice update failed.");
        }
        return noticeRepository.findById(validatedNotice.getId());
    }

    /**
     * delete notice by id
     * @param noticeId notice id
     * @author janith
     */
    public void deleteNotice(int noticeId) {
        if (noticeId <= 0) {
            throw new RuntimeException("Invalid notice ID.");
        }
        if (!noticeRepository.deleteById(noticeId)) {
            throw new RuntimeException("Notice delete failed.");
        }
    }

    /**
     * create notice entity from add/edit request payload
     * @param request add notice request
     * @author janith
     */
    private Notice createNotice(AddNoticeRequest request) {
        Notice notice = new Notice();
        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        notice.setAudience(request.getAudience());
        notice.setPriority(request.getPriority());
        notice.setStatus(request.getStatus());
        notice.setPublishedDate(parseDate(request.getPublishedDate(), "Published date must be in yyyy-mm-dd format."));
        notice.setExpiryDate(parseOptionalDate(request.getExpiryDate()));
        notice.setCreatedBy(request.getCreatedBy());
        return notice;
    }

    /**
     * validate and normalize notice entity fields
     * @param notice notice entity
     * @param requireId require positive notice id
     * @author janith
     */
    private Notice validate(Notice notice, boolean requireId) {
        if (notice == null) {
            throw new RuntimeException("Notice details are required.");
        }
        if (requireId && notice.getId() <= 0) {
            throw new RuntimeException("Invalid notice ID.");
        }

        String title = normalize(notice.getTitle());
        String content = normalize(notice.getContent());
        String audience = normalize(notice.getAudience()).toUpperCase();
        String priority = normalize(notice.getPriority()).toUpperCase();
        String status = normalize(notice.getStatus()).toUpperCase();

        if (title.isEmpty()) {
            throw new RuntimeException("Notice title is required.");
        }
        if (content.isEmpty()) {
            throw new RuntimeException("Notice content is required.");
        }
        if (!VALID_AUDIENCE.contains(audience)) {
            throw new RuntimeException("Invalid audience.");
        }
        if (!VALID_PRIORITY.contains(priority)) {
            throw new RuntimeException("Invalid priority.");
        }
        if (!VALID_STATUS.contains(status)) {
            throw new RuntimeException("Invalid status.");
        }
        if (notice.getCreatedBy() <= 0) {
            throw new RuntimeException("Invalid creator.");
        }
        if (notice.getExpiryDate() != null && notice.getExpiryDate().before(notice.getPublishedDate())) {
            throw new RuntimeException("Expiry date cannot be before published date.");
        }

        notice.setTitle(title);
        notice.setContent(content);
        notice.setAudience(audience);
        notice.setPriority(priority);
        notice.setStatus(status);
        return notice;
    }

    /**
     * parse required date value
     * @param value date text value
     * @param message validation message
     * @author janith
     */
    private Date parseDate(String value, String message) {
        try {
            return Date.valueOf(normalize(value));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(message);
        }
    }

    /**
     * parse optional date value
     * @param value date text value
     * @author janith
     */
    private Date parseOptionalDate(String value) {
        String normalizedValue = normalize(value);
        if (normalizedValue.isEmpty()) {
            return null;
        }
        try {
            return Date.valueOf(normalizedValue);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Expiry date must be in yyyy-mm-dd format.");
        }
    }

    /**
     * normalize string values by trimming spaces
     * @param value input value
     * @author janith
     */
    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
