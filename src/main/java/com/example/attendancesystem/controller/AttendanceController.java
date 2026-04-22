package com.example.attendancesystem.controller;


import com.example.attendancesystem.common.Result;
import com.example.attendancesystem.entity.Attendance;
import com.example.attendancesystem.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    // ========== 原有方法 ==========
    @PostMapping("/add")
    public Result<Attendance> addAttendance(@RequestBody Attendance attendance) {
        try {
            Attendance saved = attendanceService.addAttendance(attendance);
            return Result.success(saved);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/student/{studentId}")
    public Result<List<Attendance>> getByStudentId(@PathVariable Long studentId) {
        return Result.success(attendanceService.getAttendanceByStudentId(studentId));
    }

    @GetMapping("/date")
    public Result<List<Attendance>> getByDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return Result.success(attendanceService.getAttendanceByDate(date));
    }

    @GetMapping("/list")
    public Result<List<Attendance>> getAll() {
        return Result.success(attendanceService.getAllAttendances());
    }

    @PutMapping("/update")
    public Result<Attendance> updateAttendance(@RequestBody Attendance attendance) {
        try {
            Attendance updated = attendanceService.updateAttendance(attendance);
            return Result.success(updated);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteAttendance(@PathVariable Long id) {
        try {
            attendanceService.deleteAttendance(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    // ========== 任务一：分页接口 ==========
    @GetMapping("/page")
    public Result<Page<Attendance>> getAttendancePage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction) {

        Sort sort = Sort.unsorted();
        if (sortBy != null) {
            // 修正：Sort.Direction.DESC 和 Sort.Direction.ASC
            Sort.Direction dir = "desc".equalsIgnoreCase(direction)
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            sort = Sort.by(dir, sortBy);
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Attendance> pageResult = attendanceService.getAttendancePage(pageable);
        return Result.success(pageResult);
    }

    // ========== 任务三：多条件查询接口 ==========
    @GetMapping("/search")
    public Result<Page<Attendance>> searchAttendances(
            @RequestParam(required = false) String studentNumber,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction) {

        Sort sort = Sort.unsorted();
        if (sortBy != null) {
            Sort.Direction dir = "desc".equalsIgnoreCase(direction)
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            sort = Sort.by(dir, sortBy);
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Attendance> result = attendanceService.searchAttendances(
                studentNumber, status, startDate, endDate, pageable);
        return Result.success(result);
    }
}