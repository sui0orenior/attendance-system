package com.example.attendancesystem.controller;

import com.example.attendancesystem.common.Result;
import com.example.attendancesystem.entity.User;
import com.example.attendancesystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public Result<String> addUser(@RequestBody User user) {
        try {
            boolean success = userService.addUser(user);
            return success ? Result.success("教师添加成功") : Result.fail("教师添加失败");
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Integer id) {
        try {
            User user = userService.getUserById(id);
            if (user != null) {
                return Result.success(user);
            } else {
                return Result.fail("用户不存在");
            }
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/username/{username}")
    public Result<User> getUserByUsername(@PathVariable String username) {
        try {
            User user = userService.getUserByUsername(username);
            if (user != null) {
                return Result.success(user);
            } else {
                return Result.fail("用户名或密码错误");
            }
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<List<User>> getAllTeachers() {
        List<User> list = userService.getAllTeachers();
        return Result.success(list);
    }

    @PutMapping("/update")
    public Result<String> updateUser(@RequestBody User user) {
        try {
            boolean success = userService.updateUser(user);
            return success ? Result.success("更新成功") : Result.fail("更新失败");
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable Integer id) {
        try {
            boolean success = userService.deleteUser(id);
            return success ? Result.success("删除成功") : Result.fail("删除失败");
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        }
    }
}