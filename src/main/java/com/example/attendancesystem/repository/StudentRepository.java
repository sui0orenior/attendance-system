package com.example.attendancesystem.repository;

import com.example.attendancesystem.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Page<Student> findByNameContainingIgnoreCaseOrStudentNoContainingIgnoreCase(
            String name, String studentNo, Pageable pageable);
}