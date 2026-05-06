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

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    // 登录接口（保留，可选）
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return Result.success(Map.of("message", "登录成功", "username", username));
        } catch (AuthenticationException e) {
            return Result.fail("用户名或密码错误");
        }
    }

    // ❌ 注释掉这个方法，因为 RegisterController 已经处理了注册
    // @PostMapping("/register")
    // public Result<String> register(@RequestBody User user) {
    //     boolean success = userService.addUser(user);
    //     return success ? Result.success("注册成功") : Result.fail("注册失败");
    // }
}