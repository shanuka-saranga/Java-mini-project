package com.fot.system.service;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;

import java.util.List;

public interface IUserService {
    User login(String email, String password);
    List<User> getAllUsers();
    User getUserById(int id);
    int getUserCount();
    int getUserCountByRole(String role);
    User addUser(AddUserRequest request);
    User updateUser(EditUserRequest request);
    boolean deleteUser(int userId);
}
