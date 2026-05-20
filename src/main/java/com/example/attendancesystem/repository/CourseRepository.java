package com.example.attendancesystem.repository;

import com.example.attendancesystem.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // 根据教师ID查询课程
    List<Course> findByTeacherId(Long teacherId);

    // 根据课程名称模糊查询
    List<Course> findByNameContaining(String name);
}