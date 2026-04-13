package com.example.attendancesystem.service.impl;

import com.example.attendancesystem.dao.UserDao;
import com.example.attendancesystem.entity.User;
import com.example.attendancesystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public boolean addUser(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        User exist = userDao.findByUsername(user.getUsername());
        if (exist != null) {
            throw new IllegalArgumentException("用户名已存在");
        }
        int rows = userDao.insert(user);
        return rows > 0;
    }

    @Override
    public User getUserById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("无效的用户ID");
        }
        return userDao.findById(id);
    }

    @Override
    public User getUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        return userDao.findByUsername(username);
    }

    @Override
    public List<User> getAllTeachers() {
        return userDao.findAllTeachers();
    }

    @Override
    public boolean updateUser(User user) {
        if (user.getId() == null || user.getId() <= 0) {
            throw new IllegalArgumentException("用户ID无效");
        }
        User exist = userDao.findById(user.getId());
        if (exist == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        int rows = userDao.update(user);
        return rows > 0;
    }

    @Override
    public boolean deleteUser(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("用户ID无效");
        }
        int rows = userDao.deleteById(id);
        return rows > 0;
    }
}