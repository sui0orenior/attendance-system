package com.example.attendancesystem.controller;


import com.example.attendancesystem.common.Result;
import com.example.attendancesystem.entity.Student;
import com.example.attendancesystem.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/add")
    public Result<Student> addStudent(@RequestBody Student student) {
        try {
            Student saved = studentService.addStudent(student);
            return Result.success(saved);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<Student> getStudent(@PathVariable Long id) {
        Optional<Student> student = studentService.getStudentById(id);
        return student.map(Result::success).orElseGet(() -> Result.fail("学生不存在"));
    }

    @GetMapping("/number/{studentNumber}")
    public Result<Student> getStudentByNumber(@PathVariable String studentNumber) {
        Optional<Student> student = studentService.getStudentByNumber(studentNumber);
        return student.map(Result::success).orElseGet(() -> Result.fail("学生不存在"));
    }

    @GetMapping("/class/{className}")
    public Result<List<Student>> getStudentsByClass(@PathVariable String className) {
        List<Student> list = studentService.getStudentsByClass(className);
        return Result.success(list);
    }

    @GetMapping("/list")
    public Result<List<Student>> getAllStudents() {
        return Result.success(studentService.getAllStudents());
    }

    @PutMapping("/update")
    public Result<Student> updateStudent(@RequestBody Student student) {
        try {
            Student updated = studentService.updateStudent(student);
            return Result.success(updated);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteStudent(@PathVariable Long id) {
        try {
            studentService.deleteStudent(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
}