package com.fot.system.model.dto;

import com.fot.system.model.entity.*;

public class EditNoticeRequest extends AddNoticeRequest {
    private final int noticeId;

    public EditNoticeRequest(int noticeId, String title, String content, String audience, String priority,
                             String status, String publishedDate, String expiryDate, int createdBy) {
        super(title, content, audience, priority, status, publishedDate, expiryDate, createdBy);
        this.noticeId = noticeId;
    }

    public int getNoticeId() {
        return noticeId;
    }
}
