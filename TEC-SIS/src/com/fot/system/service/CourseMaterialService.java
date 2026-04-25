package com.fot.system.service;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.repository.CourseMaterialRepository;

import java.util.List;

public class CourseMaterialService {

    private final CourseMaterialRepository courseMaterialRepository;
    private final CourseMaterialStorageService courseMaterialStorageService;

    /**
     * initialize material service with repository and storage handlers
     * @author poornika
     */
    public CourseMaterialService() {
        this.courseMaterialRepository = new CourseMaterialRepository();
        this.courseMaterialStorageService = new CourseMaterialStorageService();
    }

    /**
     * fetch active materials for a course
     * @param courseId course id
     * @author poornika
     */
    public List<CourseMaterial> getMaterialsByCourseId(int courseId) {
        if (courseId <= 0) {
            throw new RuntimeException("Invalid course ID.");
        }
        return courseMaterialRepository.findByCourseId(courseId);
    }

    /**
     * create and persist a new course material
     * @param request add material payload
     * @author poornika
     */
    public CourseMaterial addMaterial(AddCourseMaterialRequest request) {
        CourseMaterial material = validate(createMaterial(request));
        if (!courseMaterialRepository.save(material)) {
            throw new RuntimeException("Course material add failed.");
        }
        return courseMaterialRepository.findById(material.getId());
    }

    /**
     * update existing material details
     * @param request edit material payload
     * @author poornika
     */
    public CourseMaterial updateMaterial(EditCourseMaterialRequest request) {
        CourseMaterial existingMaterial = courseMaterialRepository.findById(request.getMaterialId());
        if (existingMaterial == null) {
            throw new RuntimeException("Course material not found.");
        }

        CourseMaterial material = validate(updateMaterialDetails(request, existingMaterial));
        if (!courseMaterialRepository.update(material)) {
            throw new RuntimeException("Course material update failed.");
        }
        return courseMaterialRepository.findById(material.getId());
    }

    /**
     * archive material and clean up managed file
     * @param materialId material id
     * @author poornika
     */
    public void deleteMaterial(int materialId) {
        CourseMaterial existingMaterial = courseMaterialRepository.findById(materialId);
        if (existingMaterial == null) {
            throw new RuntimeException("Course material not found.");
        }

        if (!courseMaterialRepository.archive(materialId)) {
            throw new RuntimeException("Course material remove failed.");
        }

        courseMaterialStorageService.deleteStoredMaterialFile(existingMaterial.getFilePath());
    }

    /**
     * map add request into course material entity
     * @param request add material payload
     * @author poornika
     */
    private CourseMaterial createMaterial(AddCourseMaterialRequest request) {
        CourseMaterial material = new CourseMaterial();
        material.setCourseId(parsePositiveInt(request.getCourseId(), "Course is required."));
        material.setTitle(request.getTitle());
        material.setDescription(request.getDescription());
        material.setFilePath(courseMaterialStorageService.saveMaterialFile(
                request.getFilePath(),
                request.getCourseCode(),
                request.getTitle()
        ));
        material.setFileType(request.getFileType());
        material.setUploadedBy(request.getUploadedBy());
        material.setStatus("ACTIVE");
        return material;
    }

    /**
     * apply editable fields to a new entity instance
     * @param request edit material payload
     * @param existingMaterial current stored entity
     * @author poornika
     */
    private CourseMaterial updateMaterialDetails(EditCourseMaterialRequest request, CourseMaterial existingMaterial) {
        CourseMaterial material = new CourseMaterial();
        material.setId(existingMaterial.getId());
        material.setCourseId(existingMaterial.getCourseId());
        material.setTitle(request.getTitle());
        material.setDescription(request.getDescription());
        material.setFilePath(resolveMaterialFilePath(request, existingMaterial));
        material.setFileType(request.getFileType());
        material.setUploadedBy(request.getUploadedBy());
        material.setUploadedAt(existingMaterial.getUploadedAt());
        material.setStatus(existingMaterial.getStatus());
        return material;
    }

    /**
     * resolve file path for update and rotate file if changed
     * @param request edit material payload
     * @param existingMaterial current stored entity
     * @author poornika
     */
    private String resolveMaterialFilePath(EditCourseMaterialRequest request, CourseMaterial existingMaterial) {
        String requestedPath = normalize(request.getFilePath());
        String existingPath = normalize(existingMaterial.getFilePath());
        if (requestedPath.equals(existingPath)) {
            return existingPath;
        }

        String savedPath = courseMaterialStorageService.saveMaterialFile(
                requestedPath,
                request.getCourseCode(),
                request.getTitle()
        );
        courseMaterialStorageService.deleteStoredMaterialFile(existingPath);
        return savedPath;
    }

    /**
     * validate and normalize material fields
     * @param material material entity
     * @author poornika
     */
    private CourseMaterial validate(CourseMaterial material) {
        if (material == null) {
            throw new RuntimeException("Material details are required.");
        }
        if (normalize(material.getTitle()).isEmpty()) {
            throw new RuntimeException("Material title is required.");
        }
        if (normalize(material.getFilePath()).isEmpty()) {
            throw new RuntimeException("File path is required.");
        }
        if (material.getUploadedBy() <= 0) {
            throw new RuntimeException("Invalid uploader.");
        }

        material.setTitle(normalize(material.getTitle()));
        material.setDescription(normalize(material.getDescription()));
        material.setFilePath(normalize(material.getFilePath()));
        material.setFileType(normalize(material.getFileType()));
        return material;
    }

    /**
     * parse positive integer value or throw error
     * @param value raw numeric value
     * @param message validation message
     * @author poornika
     */
    private int parsePositiveInt(String value, String message) {
        try {
            int parsedValue = Integer.parseInt(normalize(value));
            if (parsedValue <= 0) {
                throw new RuntimeException(message);
            }
            return parsedValue;
        } catch (NumberFormatException e) {
            throw new RuntimeException(message);
        }
    }

    /**
     * trim string values safely
     * @param value raw value
     * @author poornika
     */
    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
