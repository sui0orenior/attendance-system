package com.example.attendancesystem.controller;

import com.example.attendancesystem.common.Result;
import com.example.attendancesystem.entity.User;
import com.example.attendancesystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;  // 新增

    // ==================== 新增：登录接口 ====================
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        try {
            // 1. 把用户名密码交给 Spring Security 验证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // 2. 验证通过，保存登录状态
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 3. 返回成功信息
            return Result.success(Map.of(
                    "message", "登录成功",
                    "username", username,
                    "roles", authentication.getAuthorities().toString()
            ));

        } catch (AuthenticationException e) {
            return Result.fail("用户名或密码错误");
        }
    }

    // ==================== 把原来的 add 改成 register ====================
    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        try {
            boolean success = userService.addUser(user);
            return success ? Result.success("注册成功") : Result.fail("注册失败");
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        }
    }

    // ========== 下面的方法保持原样 ==========
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        return user != null ? Result.success(user) : Result.fail("用户不存在");
    }

    @GetMapping("/username/{username}")
    public Result<User> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        return user != null ? Result.success(user) : Result.fail("用户不存在");
    }

    @GetMapping("/list")
    public Result<List<User>> getAllTeachers() {
        return Result.success(userService.getAllTeachers());
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