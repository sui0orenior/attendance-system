package com.example.attendancesystem.service.impl;


import com.example.attendancesystem.entity.Attendance;
import com.example.attendancesystem.responsitory.AttendanceRepository;
import com.example.attendancesystem.service.AttendanceService;
import com.example.attendancesystem.specification.AttendanceSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Override
    public Attendance addAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    @Override
    public List<Attendance> getAttendanceByStudentId(Long studentId) {
        return attendanceRepository.findByStudentId(studentId);
    }

    @Override
    public List<Attendance> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByAttendanceDate(date);
    }

    @Override
    public List<Attendance> getAllAttendances() {
        return attendanceRepository.findAll();
    }

    // ========== 任务一：分页查询 ==========
    @Override
    public Page<Attendance> getAttendancePage(Pageable pageable) {
        return attendanceRepository.findAll(pageable);
    }

    // ========== 任务三：多条件查询 ==========
    @Override
    public Page<Attendance> searchAttendances(String studentNumber, String status,
                                              LocalDate startDate, LocalDate endDate,
                                              Pageable pageable) {  // 确保参数完整
        Specification<Attendance> spec = Specification
                .where(AttendanceSpecifications.hasStudentNumber(studentNumber))
                .and(AttendanceSpecifications.hasStatus(status))
                .and(AttendanceSpecifications.hasDateBetween(startDate, endDate));
        return attendanceRepository.findAll(spec, pageable);
    }

    @Override
    public Attendance updateAttendance(Attendance attendance) {
        if (attendance.getId() == null || !attendanceRepository.existsById(attendance.getId())) {
            throw new IllegalArgumentException("考勤记录不存在");
        }
        return attendanceRepository.save(attendance);
    }

    @Override
    public void deleteAttendance(Long id) {
        attendanceRepository.deleteById(id);
    }
}