package com.fot.system.service;

import com.fot.system.config.AppConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;

public class CourseMaterialStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".pdf", ".doc", ".docx", ".ppt", ".pptx", ".xls", ".xlsx", ".txt", ".zip", ".rar",
            ".jpg", ".jpeg", ".png"
    );

    /**
     * copy selected material file into managed storage
     * @param sourcePath local source path
     * @param courseCode course code
     * @param title material title
     * @author poornika
     */
    public String saveMaterialFile(String sourcePath, String courseCode, String title) {
        String normalizedSourcePath = normalize(sourcePath);
        if (normalizedSourcePath.isEmpty()) {
            return null;
        }

        Path source = Paths.get(normalizedSourcePath);
        if (!Files.exists(source) || Files.isDirectory(source)) {
            throw new RuntimeException("Selected material file does not exist.");
        }

        String extension = getExtension(source.getFileName().toString());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new RuntimeException("Unsupported material file type.");
        }

        try {
            Path targetDirectory = Paths.get(AppConfig.COURSE_MATERIAL_DIR);
            Files.createDirectories(targetDirectory);

            String fileName = buildFileName(courseCode, title, extension);
            Path target = targetDirectory.resolve(fileName);
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            return target.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save material file: " + e.getMessage(), e);
        }
    }

    /**
     * delete stored managed file if available
     * @param filePath managed file path
     * @author poornika
     */
    public void deleteStoredMaterialFile(String filePath) {
        String normalizedPath = normalize(filePath);
        if (normalizedPath.isEmpty()) {
            return;
        }

        try {
            Path target = Paths.get(normalizedPath).toAbsolutePath().normalize();
            Path managedDirectory = Paths.get(AppConfig.COURSE_MATERIAL_DIR).toAbsolutePath().normalize();
            if (!target.startsWith(managedDirectory)) {
                return;
            }

            Files.deleteIfExists(target);
        } catch (IOException ignored) {
            // Material archive should still succeed even if physical file cleanup fails.
        }
    }

    /**
     * build safe unique file name for managed storage
     * @param courseCode course code
     * @param title material title
     * @param extension file extension
     * @author poornika
     */
    private String buildFileName(String courseCode, String title, String extension) {
        String safeCourseCode = normalize(courseCode).toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "_");
        String safeTitle = normalize(title).toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "_");
        return safeCourseCode + "_" + safeTitle + "_" + System.currentTimeMillis() + extension;
    }

    /**
     * resolve lowercase extension from file name
     * @param fileName source file name
     * @author poornika
     */
    private String getExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex < 0) {
            return "";
        }
        return fileName.substring(lastDotIndex).toLowerCase(Locale.ROOT);
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
