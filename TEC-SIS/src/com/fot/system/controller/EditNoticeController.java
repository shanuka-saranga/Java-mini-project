package com.fot.system.controller;

import com.fot.system.model.EditNoticeRequest;
import com.fot.system.model.Notice;
import com.fot.system.service.NoticeService;

public class EditNoticeController {

    private final NoticeService noticeService;

    public EditNoticeController() {
        this.noticeService = new NoticeService();
    }

    public Notice updateNotice(EditNoticeRequest request) {
        validate(request);
        return noticeService.updateNotice(request);
    }

    public void deleteNotice(int noticeId) {
        if (noticeId <= 0) {
            throw new RuntimeException("Invalid notice ID.");
        }
        noticeService.deleteNotice(noticeId);
    }

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
        requireValue(request.getPublishedDate(), "Published date is required.");
    }

    private void requireValue(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException(message);
        }
    }
}
