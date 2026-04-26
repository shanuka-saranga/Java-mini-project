package com.fot.system.controller;

import com.fot.system.model.dto.EditNoticeRequest;
import com.fot.system.model.entity.Notice;
import com.fot.system.service.NoticeService;

import java.sql.Date;
import java.util.Set;

/**
 * handle edit and delete notice actions with validation
 * @author janith
 */
public class EditNoticeController {
    private static final Set<String> VALID_AUDIENCE = Set.of("ALL", "STUDENT", "LECTURER", "TO");
    private static final Set<String> VALID_PRIORITY = Set.of("LOW", "MEDIUM", "HIGH");
    private static final Set<String> VALID_STATUS = Set.of("ACTIVE", "INACTIVE");

    private final NoticeService noticeService;

    /**
     * initialize edit notice controller
     * @author janith
     */
    public EditNoticeController() {
        this.noticeService = new NoticeService();
    }

    /**
     * validate and update notice record
     * @param request edit notice request payload
     * @author janith
     */
    public Notice updateNotice(EditNoticeRequest request) {
        validate(request);
        return noticeService.updateNotice(request);
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
        noticeService.deleteNotice(noticeId);
    }

    /**
     * validate edit notice request fields
     * @param request edit notice request payload
     * @author janith
     */
    private void validate(EditNoticeRequest request) {
        if (request == null) {
            throw new RuntimeException("Notice request cannot be null.");
        }
        if (request.getNoticeId() <= 0) {
            throw new RuntimeException("Invalid notice ID.");
        }
        if (request.getCreatedBy() <= 0) {
            throw new RuntimeException("Invalid creator.");
        }

        requireValue(request.getTitle(), "Notice title is required.");
        requireValue(request.getContent(), "Notice content is required.");
        requireValue(request.getAudience(), "Audience is required.");
        requireValue(request.getPriority(), "Priority is required.");
        requireValue(request.getStatus(), "Status is required.");
        requireValue(request.getPublishedDate(), "Published date is required.");

        validateAudience(request.getAudience());
        validatePriority(request.getPriority());
        validateStatus(request.getStatus());

        Date publishedDate = parseDate(request.getPublishedDate(), "Published date must be in yyyy-mm-dd format.");
        Date expiryDate = parseOptionalDate(request.getExpiryDate());
        if (expiryDate != null && expiryDate.before(publishedDate)) {
            throw new RuntimeException("Expiry date cannot be before published date.");
        }
    }

    /**
     * ensure required values are present
     * @param value value to check
     * @param message validation message
     * @author janith
     */
    private void requireValue(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException(message);
        }
    }

    /**
     * validate audience option
     * @param audience audience value
     * @author janith
     */
    private void validateAudience(String audience) {
        String normalized = normalize(audience).toUpperCase();
        if (!VALID_AUDIENCE.contains(normalized)) {
            throw new RuntimeException("Invalid audience.");
        }
    }

    /**
     * validate priority option
     * @param priority priority value
     * @author janith
     */
    private void validatePriority(String priority) {
        String normalized = normalize(priority).toUpperCase();
        if (!VALID_PRIORITY.contains(normalized)) {
            throw new RuntimeException("Invalid priority.");
        }
    }

    /**
     * validate status option
     * @param status status value
     * @author janith
     */
    private void validateStatus(String status) {
        String normalized = normalize(status).toUpperCase();
        if (!VALID_STATUS.contains(normalized)) {
            throw new RuntimeException("Invalid status.");
        }
    }

    /**
     * parse required date value
     * @param value date value
     * @param message validation message
     * @author janith
     */
    private Date parseDate(String value, String message) {
        try {
            return Date.valueOf(normalize(value));
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException(message);
        }
    }

    /**
     * parse optional date value
     * @param value date value
     * @author janith
     */
    private Date parseOptionalDate(String value) {
        String normalized = normalize(value);
        if (normalized.isEmpty()) {
            return null;
        }
        try {
            return Date.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Expiry date must be in yyyy-mm-dd format.");
        }
    }

    /**
     * normalize text by trimming spaces
     * @param value input value
     * @author janith
     */
    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
