package com.fot.system.service;

import com.fot.system.config.AppConfig;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.repository.UserRepository;

import java.sql.Date;
import java.util.List;

public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final ProfilePictureStorageService profilePictureStorageService;

    public UserService() {
        this.userRepository = new UserRepository();
        this.profilePictureStorageService = new ProfilePictureStorageService();
    }

    /**
     * login users
     * @param email user email
     * @param password user password
     * @author methum
     */
    public User login(String email, String password) {
        email = email == null ? null : email.trim();

        if (email == null || email.isEmpty()) {
            throw new RuntimeException("Email is required");
        }

        if (password == null || password.isEmpty()) {
            throw new RuntimeException("Password is required");
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            return null;
        }

        if (!user.getPasswordHash().equals(password)) {
            return null;
        }

        if (!AppConfig.STATUS_ACTIVE.equalsIgnoreCase(user.getStatus())) {
            System.out.println("user status is = "+ user.getStatus());
            throw new RuntimeException("User account is blocked");
        }

        return user;
    }

    /**
     * get all users
     * @author janith
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * get user by id
     * @param id user id
     * @author janith , methum, poornika , shaanuka
     */
    public User getUserById(int id) {
        return userRepository.findById(id);
    }

    /**
     * get user count
     * @author janith
     */
    public int getUserCount() {
        return userRepository.countAll();
    }

    /**
     * get user count for each role
     * @param role user role (ADMIN / DEAN / LECTURER / STUDENT / TO)
     * @author janith
     */
    public int getUserCountByRole(String role) {
        return userRepository.countByRole(role);
    }

    /**
     * add new user
     * @param request user details to add
     * @author janith
     */
    public User addUser(AddUserRequest request) {
        User user = createUserByRole(request);
        boolean saved = userRepository.save(user);

        if (!saved) {
            throw new RuntimeException("User registration failed.");
        }

        return user;
    }

    /**
     * update user details
     * @param request user details to update
     * @author janith
     */
    public User updateUser(EditUserRequest request) {
        User user = createUserByRole(request);
        boolean updated = userRepository.update(user);

        if (!updated) {
            throw new RuntimeException("User update failed.");
        }

        return userRepository.findById(user.getId());
    }

    /**
     * delete user
     * @param userId id of the user to delete
     * @author janith
     */
    public boolean deleteUser(int userId) {
        if (userId <= 0) {
            throw new RuntimeException("Invalid user ID.");
        }

        return userRepository.deleteById(userId);
    }

    /**
     * Checks whether a user email already exists.
     * @param email email to check
     * @return {@code true} when the email already exists
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Checks whether a user email exists for another user record.
     * @param email  email to check
     * @param userId current user id to exclude
     * @return {@code true} when another user already has this email
     */
    public boolean emailExistsExcludingUserId(String email, int userId) {
        return userRepository.existsByEmailExcludingUserId(email, userId);
    }

    /**
     * Checks whether a phone number already exists.
     * @param phone phone number to check
     * @return {@code true} when the phone number already exists
     */
    public boolean phoneExists(String phone) {
        return userRepository.existsByPhone(phone);
    }

    /**
     * Checks whether a phone number exists for another user record.
     * @param phone  phone number to check
     * @param userId current user id to exclude
     * @return {@code true} when another user already has this phone number
     */
    public boolean phoneExistsExcludingUserId(String phone, int userId) {
        return userRepository.existsByPhoneExcludingUserId(phone, userId);
    }

    /**
     * Checks whether a student registration number already exists.
     * @param registrationNo registration number to check
     * @return {@code true} when the registration number already exists
     */
    public boolean registrationNoExists(String registrationNo) {
        return userRepository.existsByRegistrationNo(registrationNo);
    }

    /**
     * Checks whether a student registration number exists for another user record.
     * @param registrationNo registration number to check
     * @param userId         current user id to exclude
     * @return {@code true} when another user already has this registration number
     */
    public boolean registrationNoExistsExcludingUserId(String registrationNo, int userId) {
        return userRepository.existsByRegistrationNoExcludingUserId(registrationNo, userId);
    }

    /**
     * Checks whether a staff code already exists.
     * @param staffCode staff code to check
     * @return {@code true} when the staff code already exists
     */
    public boolean staffCodeExists(String staffCode) {
        return userRepository.existsByStaffCode(staffCode);
    }

    /**
     * Checks whether a staff code exists for another user record.
     * @param staffCode staff code to check
     * @param userId    current user id to exclude
     * @return {@code true} when another user already has this staff code
     */
    public boolean staffCodeExistsExcludingUserId(String staffCode, int userId) {
        return userRepository.existsByStaffCodeExcludingUserId(staffCode, userId);
    }

    /**
     * create user considering user role
     * @param request AdduserRequest object
     * @author janith
     */
    private User createUserByRole(AddUserRequest request) {
        if (request.isStudentRole()) {
            Student student = new Student();
            populateCommonFields(student, request);
            student.setRegistrationNo(request.getRegistrationNo());
            student.setRegistrationYear(parseRegistrationYear(request.getRegistrationYear()));
            student.setStudentType(request.getStudentType());
            return student;
        }

        Staff staff = new Staff();
        populateCommonFields(staff, request);
        staff.setStaffCode(request.getStaffCode());
        staff.setDesignation(request.getDesignation());
        return staff;
    }

    /**
     * create user considering user role
     * @param request EditUserRequest object
     * @author janith
     */
    private User createUserByRole(EditUserRequest request) {
        if (request.isStudentRole()) {
            Student student = new Student();
            student.setId(request.getUserId());
            populateCommonFields(student, request);
            student.setRegistrationNo(request.getRegistrationNo());
            student.setRegistrationYear(parseRegistrationYear(request.getRegistrationYear()));
            student.setStudentType(request.getStudentType());
            return student;
        }

        Staff staff = new Staff();
        staff.setId(request.getUserId());
        populateCommonFields(staff, request);
        staff.setStaffCode(request.getStaffCode());
        staff.setDesignation(request.getDesignation());
        return staff;
    }

    /**
     * populate common user fields for add operation
     * @param user user entity to populate
     * @param request AddUserRequest object containing user details
     * @author janith
     */
    private void populateCommonFields(User user, AddUserRequest request) {
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(request.getPassword());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setProfilePicturePath(saveProfilePictureIfPresent(
                request.getProfilePicturePath(),
                request.getEmail(),
                request.getRole()
        ));
        user.setDob(parseDob(request.getDob()));
        user.setRole(request.getRole());
        user.setStatus(normalizeStatus(request.getStatus()));
        user.setDepartmentId(parseDepartmentId(request.getDepartmentId()));
    }

    /**
     * populate common user fields for edit operation
     * @param user user entity to populate
     * @param request EditUserRequest object containing user details
     * @author janith
     */
    private void populateCommonFields(User user, EditUserRequest request) {
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(request.getPassword());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setProfilePicturePath(resolveProfilePicturePathForUpdate(
                request.getProfilePicturePath(),
                request.getEmail(),
                request.getRole()
        ));
        user.setDob(parseDob(request.getDob()));
        user.setRole(request.getRole());
        user.setStatus(normalizeStatus(request.getStatus()));
        user.setDepartmentId(parseDepartmentId(request.getDepartmentId()));
    }

    /**
     * parse department id from string to int
     * @param departmentId department id as string
     * @author janith
     */
    private int parseDepartmentId(String departmentId) {
        try {
            return Integer.parseInt(departmentId.trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Department ID must be a valid number.");
        }
    }

    /**
     * parse date of birth from string to Date
     * @param dob date of birth as string
     * @author janith
     */
    private Date parseDob(String dob) {
        try {
            return Date.valueOf(dob.trim());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("DOB must be in yyyy-mm-dd format.");
        }
    }

    /**
     * parse registration year from string to int
     * @param registrationYear registration year as string
     * @author janith
     */
    private int parseRegistrationYear(String registrationYear) {
        try {
            return Integer.parseInt(registrationYear.trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Registration year must be a valid year.");
        }
    }

    /**
     * normalize user status to allowed values (ACTIVE, SUSPENDED, BLOCKED)
     * @param status user status as string
     * @author janith
     */
    private String normalizeStatus(String status) {
        if ("SUSPENDED".equalsIgnoreCase(status)) {
            return "SUSPENDED";
        }
        if (AppConfig.STATUS_BLOCKED.equalsIgnoreCase(status)) {
            return AppConfig.STATUS_BLOCKED;
        }
        return AppConfig.STATUS_ACTIVE;
    }

    /**
     * save profile picture if a path is provided, otherwise return null
     * @param sourcePath original path of the profile picture
     * @param email user email (used for naming the stored picture)
     * @param role user role (used for organizing stored pictures)
     * @author janith
     */
    private String saveProfilePictureIfPresent(String sourcePath, String email, String role) {
        if (sourcePath == null || sourcePath.trim().isEmpty()) {
            return null;
        }
        return profilePictureStorageService.saveProfilePicture(sourcePath, email, role);
    }

    /**
     * resolve profile picture path for update - if the provided path is already managed, return it as is, otherwise save the new picture and return the new path
     * @param picturePath provided profile picture path
     * @param email user email (used for naming the stored picture if saving is needed)
     * @param role user role (used for organizing stored pictures if saving is needed)
     * @author janith
     */
    private String resolveProfilePicturePathForUpdate(String picturePath, String email, String role) {
        if (picturePath == null || picturePath.trim().isEmpty()) {
            return null;
        }

        if (profilePictureStorageService.isManagedProfilePicture(picturePath)) {
            return picturePath.trim();
        }

        return profilePictureStorageService.saveProfilePicture(picturePath, email, role);
    }

}
