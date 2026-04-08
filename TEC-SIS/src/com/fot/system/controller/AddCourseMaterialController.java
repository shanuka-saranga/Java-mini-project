package com.fot.system.controller;

import com.fot.system.model.AddCourseMaterialRequest;
import com.fot.system.model.CourseMaterial;
import com.fot.system.service.CourseMaterialService;

public class AddCourseMaterialController {

    private final CourseMaterialService courseMaterialService;

    public AddCourseMaterialController() {
        this.courseMaterialService = new CourseMaterialService();
    }

    public CourseMaterial addMaterial(AddCourseMaterialRequest request) {
        validateRequest(request);
        return courseMaterialService.addMaterial(request);
    }

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

    private void requireValue(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException(message);
        }
    }
}
