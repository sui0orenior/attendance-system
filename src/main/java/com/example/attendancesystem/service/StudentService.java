package com.example.attendancesystem.service;

import com.example.attendancesystem.entity.Student;
import org.springframework.data.domain.Page;

public interface StudentService {
    // 分页查询（带搜索和排序）
    Page<Student> findAll(int page, int size, String sortBy, String sortOrder, String keyword);

    // 根据ID查询学生
    Student findById(Long id);

    // ⭐ 新增：根据学号查询学生
    Student findByStudentNo(String studentNo);

    // 保存学生（新增或编辑）
    Student save(Student student);

    // 删除学生
    void deleteById(Long id);
}