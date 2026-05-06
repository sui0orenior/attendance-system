package com.example.attendancesystem.controller;

import com.example.attendancesystem.entity.User;
import com.example.attendancesystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class RegisterController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegister() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(
            @RequestParam String username,
            @RequestParam String realName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam String password,
            @RequestParam String confirmPassword,    // ← 新增：接收确认密码
            RedirectAttributes redirectAttributes) {

        // ← 新增：校验两次密码是否一致
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMsg", "两次密码不一致，请重新输入");
            redirectAttributes.addFlashAttribute("username", username);
            redirectAttributes.addFlashAttribute("realName", realName);
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("phone", phone);
            return "redirect:/user/register";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRealName(realName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole("TEACHER");

        boolean success = userService.addUser(user);
        if (success) {
            return "redirect:/login?registerSuccess";
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "注册失败，用户名可能已存在");
            redirectAttributes.addFlashAttribute("username", username);
            redirectAttributes.addFlashAttribute("realName", realName);
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("phone", phone);
            return "redirect:/user/register";
        }
    }
}