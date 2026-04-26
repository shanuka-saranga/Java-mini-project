package com.fot.system.service;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;

import java.util.List;

/**
 * define user service operations used by admin and profile flows
 * @author janith
 */
public interface IUserService {
    /**
     * login user by credentials
     * @param email email value
     * @param password password value
     * @author janith
     */
    User login(String email, String password);

    /**
     * get all users
     * @author janith
     */
    List<User> getAllUsers();

    /**
     * get user by id
     * @param id user id
     * @author janith
     */
    User getUserById(int id);

    /**
     * get total user count
     * @author janith
     */
    int getUserCount();

    /**
     * get user count by role
     * @param role role name
     * @author janith
     */
    int getUserCountByRole(String role);

    /**
     * add user
     * @param request add user payload
     * @author janith
     */
    User addUser(AddUserRequest request);

    /**
     * update user
     * @param request edit user payload
     * @author janith
     */
    User updateUser(EditUserRequest request);

    /**
     * delete user by id
     * @param userId user id
     * @author janith
     */
    boolean deleteUser(int userId);
}
