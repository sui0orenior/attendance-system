package com.example.attendancesystem.repository;

import com.example.attendancesystem.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long>,
        JpaSpecificationExecutor<Attendance> {

    List<Attendance> findByStudentId(Long studentId);
    List<Attendance> findByAttendanceDate(LocalDate date);
    List<Attendance> findByStudentIdAndCourseName(Long studentId, String courseName);
}