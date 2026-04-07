package com.fot.system.model;

public class AddNoticeRequest {
    private final String title;
    private final String content;
    private final String audience;
    private final String priority;
    private final String status;
    private final String publishedDate;
    private final String expiryDate;
    private final int createdBy;

    public AddNoticeRequest(String title, String content, String audience, String priority,
                            String status, String publishedDate, String expiryDate, int createdBy) {
        this.title = title;
        this.content = content;
        this.audience = audience;
        this.priority = priority;
        this.status = status;
        this.publishedDate = publishedDate;
        this.expiryDate = expiryDate;
        this.createdBy = createdBy;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getAudience() {
        return audience;
    }

    public String getPriority() {
        return priority;
    }

    public String getStatus() {
        return status;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public int getCreatedBy() {
        return createdBy;
    }
}
