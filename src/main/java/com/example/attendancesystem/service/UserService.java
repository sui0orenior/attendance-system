package com.example.attendancesystem.service;

import com.example.attendancesystem.entity.User;
import java.util.List;

public interface UserService {
    boolean addUser(User user);
    User getUserById(Integer id);
    User getUserByUsername(String username);
    List<User> getAllTeachers();
    boolean updateUser(User user);
    boolean deleteUser(Integer id);
}