package com.example.attendancesystem.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 总配置
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // 开启 @PreAuthorize 注解
public class SecurityConfig {

    // 密码加密器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 认证管理器（登录时用）
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // 安全规则配置
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // ===== 路径权限规则 =====
                .authorizeHttpRequests(auth -> auth
                        // 这两个路径，不登录也能访问
                        .requestMatchers("/user/register", "/user/login").permitAll()

                        // 学生管理：只有 ADMIN 和 TEACHER 能访问
                        .requestMatchers("/student/**").hasAnyRole("ADMIN", "TEACHER")

                        // 考勤管理：ADMIN、TEACHER、STUDENT 都能访问
                        .requestMatchers("/attendance/**").hasAnyRole("ADMIN", "TEACHER", "STUDENT")

                        // 其他所有请求，必须登录
                        .anyRequest().authenticated()
                )

                // ===== 登录配置 =====
                .formLogin(form -> form
                        .defaultSuccessUrl("/attendance/list")  // 登录成功后跳转
                        .permitAll()
                )

                // ===== 关闭 CSRF（练习阶段省事） =====
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}