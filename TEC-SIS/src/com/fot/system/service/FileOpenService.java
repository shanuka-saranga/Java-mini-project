package com.fot.system.service;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class FileOpenService {

    public void openFile(String filePath) {
        String normalizedPath = normalize(filePath);
        if (normalizedPath.isEmpty()) {
            throw new RuntimeException("Material file path is empty.");
        }

        File file = new File(normalizedPath);
        if (!file.exists() || !file.isFile()) {
            throw new RuntimeException("Selected material file does not exist.");
        }

        if (!Desktop.isDesktopSupported()) {
            throw new RuntimeException("Desktop file opening is not supported on this system.");
        }

        Desktop desktop = Desktop.getDesktop();
        if (!desktop.isSupported(Desktop.Action.OPEN)) {
            throw new RuntimeException("Open action is not supported on this system.");
        }

        try {
            desktop.open(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to open file: " + e.getMessage(), e);
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
