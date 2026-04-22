package com.example.attendancesystem.service;

import com.example.attendancesystem.entity.Student;
import java.util.List;
import java.util.Optional;

public interface StudentService {
    Student addStudent(Student student);
    Optional<Student> getStudentById(Long id);
    Optional<Student> getStudentByNumber(String studentNumber);
    List<Student> getStudentsByClass(String className);
    List<Student> getAllStudents();
    Student updateStudent(Student student);
    void deleteStudent(Long id);
}