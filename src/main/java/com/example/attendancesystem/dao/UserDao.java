package com.example.attendancesystem.dao;

import com.example.attendancesystem.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int insert(User user) {
        String sql = "INSERT INTO teacher (username, password, real_name, email, phone) VALUES (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                user.getUsername(),
                user.getPassword(),
                user.getRealName(),
                user.getEmail(),
                user.getPhone());
    }

    public User findById(Integer id) {
        String sql = "SELECT id, username, password, real_name AS realName, email, phone, create_time AS createTime FROM teacher WHERE id = ?";
        List<User> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class), id);
        return list.isEmpty() ? null : list.get(0);
    }

    public User findByUsername(String username) {
        String sql = "SELECT id, username, password, real_name AS realName, email, phone, create_time AS createTime FROM teacher WHERE username = ?";
        List<User> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class), username);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<User> findAllTeachers() {
        String sql = "SELECT id, username, password, real_name AS realName, email, phone, create_time AS createTime FROM teacher";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
    }


    public int update(User user) {
        String sql = "UPDATE teacher SET username = ?, password = ?, real_name = ?, email = ?, phone = ? WHERE id = ?";
        return jdbcTemplate.update(sql,
                user.getUsername(),
                user.getPassword(),
                user.getRealName(),
                user.getEmail(),
                user.getPhone(),
                user.getId());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM teacher WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}