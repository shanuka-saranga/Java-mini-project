package com.fot.system.service;

import com.fot.system.config.AppConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;

public class ProfilePictureStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif");

    public String saveProfilePicture(String sourcePath, String email, String role) {
        String normalizedSourcePath = normalize(sourcePath);
        if (normalizedSourcePath.isEmpty()) {
            return null;
        }

        Path source = Paths.get(normalizedSourcePath);
        if (!Files.exists(source) || Files.isDirectory(source)) {
            throw new RuntimeException("Selected profile picture does not exist.");
        }

        String extension = getExtension(source.getFileName().toString());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new RuntimeException("Profile picture must be a JPG, JPEG, PNG, or GIF file.");
        }

        try {
            Path targetDirectory = Paths.get(AppConfig.PROFILE_PICTURE_DIR);
            Files.createDirectories(targetDirectory);

            String fileName = buildFileName(email, role, extension);
            Path target = targetDirectory.resolve(fileName);
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            return target.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save profile picture: " + e.getMessage(), e);
        }
    }

    public boolean isManagedProfilePicture(String path) {
        String normalizedPath = normalize(path);
        if (normalizedPath.isEmpty()) {
            return false;
        }
        return normalizedPath.startsWith(Paths.get(AppConfig.PROFILE_PICTURE_DIR).toAbsolutePath().toString());
    }

    private String buildFileName(String email, String role, String extension) {
        String safeEmail = normalize(email).toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "_");
        String safeRole = normalize(role).toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "_");
        return safeRole + "_" + safeEmail + "_" + System.currentTimeMillis() + extension;
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
