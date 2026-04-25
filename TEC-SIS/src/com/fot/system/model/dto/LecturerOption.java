package com.fot.system.model.dto;

public class LecturerOption {
    private final String id;
    private final String name;

    public LecturerOption(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
