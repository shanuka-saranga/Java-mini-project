package com.fot.system.config;

import java.nio.file.Path;

public class AppConfig {

    // user roles
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_DEAN = "DEAN";
    public static final String ROLE_STUDENT = "STUDENT";
    public static final String ROLE_LECTURER = "LECTURER";
    public static final String ROLE_TO = "TO";

    // local storage

    public static final String APP_STORAGE_DIR =
            Path.of(System.getProperty("user.dir"), "storage").toString();

    public static final String PROFILE_PICTURE_DIR =
            Path.of(APP_STORAGE_DIR, "profile-pictures").toString();

    public static final String COURSE_MATERIAL_DIR =
            Path.of(APP_STORAGE_DIR, "course-materials").toString();

    public static final String MEDICAL_DOCUMENT_DIR =
            Path.of(APP_STORAGE_DIR, "medical-documents").toString();


    // user status
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_BLOCKED = "BLOCKED";


    // sidebar menu items
    public static final String MENU_HOME = "HOME";
    public static final String MENU_LOGOUT = "LOGOUT";
    public static final String MENU_MANAGE_USERS = "MANAGE_USERS";
    public static final String MENU_MANAGE_COURSES = "MANAGE_COURSES";
    public static final String MENU_MANAGE_NOTICES = "MANAGE_NOTICES";
    public static final String MENU_PROFILE = "PROFILE";
    public static final String MENU_COURSES = "COURSES";
    public static final String MENU_ATTENDANCE = "ATTENDANCE";
    public static final String MENU_STUDENTS = "STUDENTS";
    public static final String MENU_MARKS = "MARKS";
    public static final String MENU_EXAM_ELIGIBILITY = "EXAM_ELIGIBILITY";
    public static final String MENU_USERS = "USERS";
    public static final String MENU_NOTICES = "NOTICES";
    public static final String MENU_TIMETABLES = "TIMETABLES";
    public static final String MENU_REPORTS = "REPORTS";
    public static final String MENU_MEDICALS = "MEDICALS";

}
