package com.example.attendancesystem.service.impl;


import com.example.attendancesystem.entity.Student;
import com.example.attendancesystem.responsitory.StudentRepository;
import com.example.attendancesystem.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public Student addStudent(Student student) {
        if (studentRepository.findByStudentNumber(student.getStudentNumber()).isPresent()) {
            throw new IllegalArgumentException("学号已存在");
        }
        return studentRepository.save(student);
    }

    @Override
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    @Override
    public Optional<Student> getStudentByNumber(String studentNumber) {
        return studentRepository.findByStudentNumber(studentNumber);
    }

    @Override
    public List<Student> getStudentsByClass(String className) {
        return studentRepository.findByClassName(className);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();  // 修正：调用 repository
    }

    @Override
    public Student updateStudent(Student student) {
        if (student.getId() == null || !studentRepository.existsById(student.getId())) {
            throw new IllegalArgumentException("学生不存在");
        }
        return studentRepository.save(student);
    }

    @Override
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);  // 修正：方法名大小写
    }
}