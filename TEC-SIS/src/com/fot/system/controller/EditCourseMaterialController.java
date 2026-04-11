package com.fot.system.controller;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.service.CourseMaterialService;

public class EditCourseMaterialController {

    private final CourseMaterialService courseMaterialService;

    public EditCourseMaterialController() {
        this.courseMaterialService = new CourseMaterialService();
    }

    public CourseMaterial updateMaterial(EditCourseMaterialRequest request) {
        validate(request);
        return courseMaterialService.updateMaterial(request);
    }

    public void deleteMaterial(int materialId) {
        if (materialId <= 0) {
            throw new RuntimeException("Invalid material ID.");
        }
        courseMaterialService.deleteMaterial(materialId);
    }

    private void validate(EditCourseMaterialRequest request) {
        if (request == null) {
            throw new RuntimeException("Material request cannot be null.");
        }
        if (request.getMaterialId() <= 0) {
            throw new RuntimeException("Invalid material ID.");
        }
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
