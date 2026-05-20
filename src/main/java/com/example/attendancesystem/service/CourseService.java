package com.example.attendancesystem.service;

import com.example.attendancesystem.entity.Course;
import java.util.List;

public interface CourseService {
    List<Course> getAllCourses();
    Course getCourseById(Long id);
    List<Course> getCoursesByTeacherId(Long teacherId);
    Course saveCourse(Course course);
    void deleteCourse(Long id);
}