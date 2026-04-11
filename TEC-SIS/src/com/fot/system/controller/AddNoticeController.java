package com.fot.system.controller;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.service.NoticeService;
import com.fot.system.view.dashboard.admin.manageNotices.AddNewNoticePanel;

import javax.swing.*;

public class AddNoticeController {

    private final AddNewNoticePanel view;
    private final NoticeService noticeService;
    private final Runnable onSuccessAction;

    public AddNoticeController(AddNewNoticePanel view, Runnable onSuccessAction) {
        this.view = view;
        this.noticeService = new NoticeService();
        this.onSuccessAction = onSuccessAction;
        this.view.setOnSaveAction(this::handleSaveNotice);
    }

    private void handleSaveNotice() {
        try {
            AddNoticeRequest request = view.buildRequest();
            validate(request);
            Notice notice = noticeService.addNotice(request);

            JOptionPane.showMessageDialog(
                    view,
                    "\"" + notice.getTitle() + "\" added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            view.resetForm();
            if (onSuccessAction != null) {
                onSuccessAction.run();
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    view,
                    ex.getMessage(),
                    "Add Notice Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void validate(AddNoticeRequest request) {
        if (request == null) {
            throw new RuntimeException("Notice request cannot be null.");
        }
        requireValue(request.getTitle(), "Notice title is required.");
        requireValue(request.getContent(), "Notice content is required.");
        requireValue(request.getPublishedDate(), "Published date is required.");
        if (request.getCreatedBy() <= 0) {
            throw new RuntimeException("Invalid creator.");
        }
    }

    private void requireValue(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException(message);
        }
    }
}
