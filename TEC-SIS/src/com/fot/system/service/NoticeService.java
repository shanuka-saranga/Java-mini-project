package com.fot.system.service;

import com.fot.system.model.AddNoticeRequest;
import com.fot.system.model.EditNoticeRequest;
import com.fot.system.model.Notice;
import com.fot.system.repository.NoticeRepository;

import java.sql.Date;
import java.util.List;
import java.util.Set;

public class NoticeService {

    private static final Set<String> VALID_AUDIENCE = Set.of("ALL", "STUDENT", "LECTURER", "TO");
    private static final Set<String> VALID_PRIORITY = Set.of("LOW", "MEDIUM", "HIGH");
    private static final Set<String> VALID_STATUS = Set.of("ACTIVE", "INACTIVE");

    private final NoticeRepository noticeRepository;

    public NoticeService() {
        this.noticeRepository = new NoticeRepository();
    }

    public List<Notice> getAllNotices() {
        return noticeRepository.findAll();
    }

    public Notice getNoticeById(int noticeId) {
        if (noticeId <= 0) {
            throw new RuntimeException("Invalid notice ID.");
        }
        return noticeRepository.findById(noticeId);
    }

    public Notice addNotice(AddNoticeRequest request) {
        Notice notice = validate(createNotice(request), false);
        if (!noticeRepository.save(notice)) {
            throw new RuntimeException("Notice save failed.");
        }
        return noticeRepository.findById(notice.getId());
    }

    public Notice updateNotice(EditNoticeRequest request) {
        Notice notice = createNotice(request);
        notice.setId(request.getNoticeId());
        Notice validatedNotice = validate(notice, true);
        if (!noticeRepository.update(validatedNotice)) {
            throw new RuntimeException("Notice update failed.");
        }
        return noticeRepository.findById(validatedNotice.getId());
    }

    public void deleteNotice(int noticeId) {
        if (noticeId <= 0) {
            throw new RuntimeException("Invalid notice ID.");
        }
        if (!noticeRepository.deleteById(noticeId)) {
            throw new RuntimeException("Notice delete failed.");
        }
    }

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

    private Date parseDate(String value, String message) {
        try {
            return Date.valueOf(normalize(value));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(message);
        }
    }

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

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
