package com.example.attendancesystem.service.impl;

import com.example.attendancesystem.entity.Student;
import com.example.attendancesystem.repository.StudentRepository;
import com.example.attendancesystem.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public Page<Student> findAll(int page, int size, String sortBy, String sortOrder, String keyword) {
        Sort sort = Sort.unsorted();
        if (sortBy != null && !sortBy.isEmpty()) {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
            sort = Sort.by(direction, sortBy);
        }

        PageRequest pageRequest = PageRequest.of(page, size, sort);

        if (keyword != null && !keyword.isEmpty()) {
            return studentRepository.findByNameContainingIgnoreCaseOrStudentNoContainingIgnoreCase(
                    keyword, keyword, pageRequest);
        }
        return studentRepository.findAll(pageRequest);
    }

    @Override
    public Student findById(Long id) {
        Optional<Student> student = studentRepository.findById(id);
        return student.orElse(null);
    }

    // ⭐ 新增：根据学号查询学生
    @Override
    public Student findByStudentNo(String studentNo) {
        return studentRepository.findByStudentNo(studentNo);
    }

    @Override
    public Student save(Student student) {
        if (student.getCreateTime() == null) {
            student.setCreateTime(LocalDateTime.now());
        }
        return studentRepository.save(student);
    }

    @Override
    public void deleteById(Long id) {
        studentRepository.deleteById(id);
    }
}