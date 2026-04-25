package com.fot.system.controller;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.service.CourseMaterialService;

public class AddCourseMaterialController {

    private final CourseMaterialService courseMaterialService;

    /**
     * initialize add material controller
     * @author poornika
     */
    public AddCourseMaterialController() {
        this.courseMaterialService = new CourseMaterialService();
    }

    /**
     * validate and forward add-material request
     * @param request add material payload
     * @author poornika
     */
    public CourseMaterial addMaterial(AddCourseMaterialRequest request) {
        validateRequest(request);
        return courseMaterialService.addMaterial(request);
    }

    /**
     * run controller-level request validations
     * @param request add material payload
     * @author poornika
     */
    private void validateRequest(AddCourseMaterialRequest request) {
        if (request == null) {
            throw new RuntimeException("Material request cannot be null.");
        }
        requireValue(request.getCourseId(), "Course is required.");
        requireValue(request.getTitle(), "Material title is required.");
        requireValue(request.getFilePath(), "File path is required.");
        if (request.getUploadedBy() <= 0) {
            throw new RuntimeException("Invalid uploader.");
        }
    }

    /**
     * ensure required string values are present
     * @param value raw value
     * @param message validation error message
     * @author poornika
     */
    private void requireValue(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException(message);
        }
    }
}
