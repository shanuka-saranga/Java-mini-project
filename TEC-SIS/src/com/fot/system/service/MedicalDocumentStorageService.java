package com.fot.system.service;

import com.fot.system.config.AppConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;

public class MedicalDocumentStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".pdf", ".jpg", ".jpeg", ".png");

    public String saveMedicalDocument(String sourcePath, String registrationNo, String dateRangeLabel) {
        String normalizedSourcePath = normalize(sourcePath);
        if (normalizedSourcePath.isEmpty()) {
            throw new RuntimeException("Medical certificate file is required.");
        }

        Path source = Paths.get(normalizedSourcePath);
        if (!Files.exists(source) || Files.isDirectory(source)) {
            throw new RuntimeException("Selected medical certificate does not exist.");
        }

        String extension = getExtension(source.getFileName().toString());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new RuntimeException("Unsupported medical certificate file type.");
        }

        try {
            Path targetDirectory = Paths.get(AppConfig.MEDICAL_DOCUMENT_DIR);
            Files.createDirectories(targetDirectory);

            String fileName = buildFileName(registrationNo, dateRangeLabel, extension);
            Path target = targetDirectory.resolve(fileName);
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            return target.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save medical certificate: " + e.getMessage(), e);
        }
    }

    private String buildFileName(String registrationNo, String dateRangeLabel, String extension) {
        String safeRegNo = normalize(registrationNo).toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-");
        String safeRange = normalize(dateRangeLabel).toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-");
        return safeRegNo + "-" + safeRange + "-" + System.currentTimeMillis() + extension;
    }

    private String getExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex < 0) {
            return "";
        }
        return fileName.substring(lastDotIndex).toLowerCase(Locale.ROOT);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
