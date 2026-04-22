package com.example.attendancesystem.service;


import com.example.attendancesystem.entity.Attendance;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
public interface AttendanceService {
    Attendance addAttendance(Attendance attendance);
    List<Attendance> getAttendanceByStudentId(Long studentId);
    List<Attendance> getAttendanceByDate(LocalDate date);
    List<Attendance> getAllAttendances();

    // 分页查询
    Page<Attendance> getAttendancePage(Pageable pageable);

    // 多条件查询
    Page<Attendance> searchAttendances(String studentNumber, String status,
                                       LocalDate startDate, LocalDate endDate,
                                       Pageable pageable);

    Attendance updateAttendance(Attendance attendance);
    void deleteAttendance(Long id);
}