package com.example.attendancesystem.service.impl;


import com.example.attendancesystem.dao.UserDao;
import com.example.attendancesystem.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 这个类是 Spring Security 用的
 * 作用：根据用户名，去数据库查出用户的密码和角色
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserDao userDao;   // 用你原有的 UserDao 查数据库

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 查数据库
        User user = userDao.findByUsername(username);

        // 2. 查不到就报错
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 3. 把查到的用户，转成 Spring Security 认识的对象
        //    密码用数据库里已加密的
        //    角色用数据库里存的
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())   // 已加密的密码
                .roles(user.getRole())          // 角色
                .build();
    }
}