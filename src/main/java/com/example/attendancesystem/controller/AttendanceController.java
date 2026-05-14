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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PostMapping("/add")
    public Result<Attendance> addAttendance(@RequestBody Attendance attendance) {
        Attendance saved = attendanceService.addAttendance(attendance);
        return Result.success(saved);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER') or (#studentId == authentication.principal.id)")
    public Result<List<Attendance>> getByStudentId(@PathVariable Long studentId) {
        return Result.success(attendanceService.getAttendanceByStudentId(studentId));
    }

    @GetMapping("/date")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<List<Attendance>> getByDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return Result.success(attendanceService.getAttendanceByDate(date));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping("/list")
    public Result<List<Attendance>> getAll() {
        return Result.success(attendanceService.getAllAttendances());
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Attendance> updateAttendance(@RequestBody Attendance attendance) {
        Attendance updated = attendanceService.updateAttendance(attendance);
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<String> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return Result.success("删除成功");
    }

    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Page<Attendance>> getAttendancePage(
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
        Page<Attendance> pageResult = attendanceService.getAttendancePage(pageable);
        return Result.success(pageResult);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
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