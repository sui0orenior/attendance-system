package com.example.attendancesystem.controller;

import com.example.attendancesystem.common.Result;
import com.example.attendancesystem.entity.Attendance;
import com.example.attendancesystem.entity.Course;
import com.example.attendancesystem.entity.Student;
import com.example.attendancesystem.service.AttendanceService;
import com.example.attendancesystem.service.CourseService;
import com.example.attendancesystem.service.StudentService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentService studentService;

    // ==================== 页面跳转方法 ====================

    @GetMapping("/checkin")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public String checkInPage(Model model) {
        List<Course> courses = courseService.getAllCourses();
        model.addAttribute("courses", courses);
        model.addAttribute("now", LocalDateTime.now());
        return "attendance-check-in";
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public String listPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {

        Sort sort = Sort.unsorted();
        if (sortBy != null && !sortBy.isEmpty()) {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
            sort = Sort.by(direction, sortBy);
        } else {
            sort = Sort.by(Sort.Direction.DESC, "attendanceDate");
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        String currentUsername = getCurrentUsername();
        Page<Attendance> attendancePage;
        boolean isStudent = !hasRole("ADMIN") && !hasRole("TEACHER");

        if (isStudent) {
            Student student = studentService.findByStudentNo(currentUsername);
            if (student != null) {
                attendancePage = attendanceService.searchAttendances(
                        student.getStudentNo(), status, startDate, endDate, pageable);
            } else {
                attendancePage = Page.empty();
            }
        } else {
            attendancePage = attendanceService.searchAttendances(
                    keyword, status, startDate, endDate, pageable);
        }

        List<Course> courses = courseService.getAllCourses();

        model.addAttribute("records", attendancePage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", attendancePage.getTotalPages());
        model.addAttribute("totalElements", attendancePage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("keyword", keyword);
        model.addAttribute("courseId", courseId);
        model.addAttribute("status", status);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("courses", courses);

        return "attendance-list";
    }

    // ==================== 表单提交方法 ====================

    @PostMapping("/checkin")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public String processCheckIn(
            @RequestParam Long courseId,
            @RequestParam(required = false) String remark,
            RedirectAttributes redirectAttributes) {

        try {
            String username = getCurrentUsername();
            Student student = studentService.findByStudentNo(username);

            if (student == null) {
                redirectAttributes.addFlashAttribute("errorMsg", "未找到学生信息，请联系管理员");
                return "redirect:/attendance/checkin";
            }

            Course course = courseService.getCourseById(courseId);
            if (course == null) {
                redirectAttributes.addFlashAttribute("errorMsg", "课程不存在");
                return "redirect:/attendance/checkin";
            }

            List<Attendance> existing = attendanceService.getAttendanceByStudentId(student.getId());
            boolean alreadyChecked = existing.stream()
                    .anyMatch(a -> a.getAttendanceDate().equals(LocalDate.now())
                            && courseId.equals(a.getCourseId()));

            if (alreadyChecked) {
                redirectAttributes.addFlashAttribute("errorMsg", "您今天已经在此课程打卡过了！");
                return "redirect:/attendance/checkin";
            }

            Attendance attendance = new Attendance();
            attendance.setStudentId(student.getId());
            attendance.setCourseId(courseId);
            attendance.setCourseName(course.getName());
            attendance.setAttendanceDate(LocalDate.now());
            attendance.setRemark(remark);

            LocalTime now = LocalTime.now();
            LocalTime startTime = course.getStartTime();
            LocalTime endTime = course.getEndTime();

            // ⭐ 修改：传入开始时间和结束时间，支持早退判断
            String status = determineAttendanceStatus(now, startTime, endTime);
            attendance.setStatus(status);

            attendanceService.addAttendance(attendance);

            String successMsg = getSuccessMessage(status, startTime, endTime);
            redirectAttributes.addFlashAttribute("successMsg", successMsg);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "打卡失败：" + e.getMessage());
        }

        return "redirect:/attendance/list";
    }

    /**
     * 判断考勤状态（包含早退判断）
     * @param now 当前时间
     * @param startTime 上课开始时间
     * @param endTime 上课结束时间
     * @return 状态：正常/迟到/早退/缺勤
     */
    private String determineAttendanceStatus(LocalTime now, LocalTime startTime, LocalTime endTime) {
        // 早退：打卡时间早于下课时间30分钟以上，且已经上课
        if (now.isAfter(startTime) && now.isBefore(endTime.minusMinutes(30))) {
            return "早退";
        }
        // 上课前15分钟内打卡 -> 正常
        if (now.isAfter(startTime.minusMinutes(15)) && now.isBefore(startTime)) {
            return "正常";
        }
        // 上课后30分钟内打卡 -> 迟到
        else if (now.isAfter(startTime) && now.isBefore(startTime.plusMinutes(30))) {
            return "迟到";
        }
        // 超过30分钟 -> 缺勤
        else if (now.isAfter(startTime.plusMinutes(30))) {
            return "缺勤";
        }
        // 太早打卡（早于课前15分钟）-> 也是正常
        else if (now.isBefore(startTime.minusMinutes(15))) {
            return "正常";
        }
        return "正常";
    }

    private String getSuccessMessage(String status, LocalTime startTime, LocalTime endTime) {
        switch (status) {
            case "迟到":
                return "打卡成功！请注意：您已迟到，上课时间：" + startTime;
            case "早退":
                return "打卡成功！请注意：您已早退，下课时间：" + endTime;
            case "正常":
                return "打卡成功！打卡时间正常，上课时间：" + startTime;
            case "缺勤":
                return "打卡成功！但您已超过打卡时间，记为缺勤";
            default:
                return "打卡成功！";
        }
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public String deleteAttendance(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            attendanceService.deleteAttendance(id);
            redirectAttributes.addFlashAttribute("successMsg", "删除成功！");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "删除失败：" + e.getMessage());
        }
        return "redirect:/attendance/list";
    }

    // ==================== 数据导出功能 ====================

    /**
     * 导出考勤记录为Excel文件
     */
    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public void exportAttendances(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletResponse response) throws IOException {

        String currentUsername = getCurrentUsername();
        boolean isStudent = !hasRole("ADMIN") && !hasRole("TEACHER");

        Page<Attendance> attendancePage;
        Pageable pageable = PageRequest.of(0, 10000);

        if (isStudent) {
            Student student = studentService.findByStudentNo(currentUsername);
            if (student != null) {
                attendancePage = attendanceService.searchAttendances(
                        student.getStudentNo(), status, startDate, endDate, pageable);
            } else {
                attendancePage = Page.empty();
            }
        } else {
            attendancePage = attendanceService.searchAttendances(
                    keyword, status, startDate, endDate, pageable);
        }

        List<Attendance> records = attendancePage.getContent();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("考勤记录");

            // 创建标题行样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // 创建数据行样式
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // 创建标题行
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "学号", "学生姓名", "课程名称", "打卡日期", "打卡时间", "状态", "备注"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 填充数据
            int rowNum = 1;
            for (Attendance record : records) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(record.getId());
                row.createCell(1).setCellValue(record.getStudent() != null ? record.getStudent().getStudentNo() : "");
                row.createCell(2).setCellValue(record.getStudent() != null ? record.getStudent().getName() : "");
                row.createCell(3).setCellValue(record.getCourseName() != null ? record.getCourseName() : "");
                row.createCell(4).setCellValue(record.getAttendanceDate() != null ? record.getAttendanceDate().toString() : "");
                row.createCell(5).setCellValue(record.getCreateTime() != null ? record.getCreateTime().toLocalTime().toString() : "");
                row.createCell(6).setCellValue(record.getStatus() != null ? record.getStatus() : "");
                row.createCell(7).setCellValue(record.getRemark() != null ? record.getRemark() : "");

                // 应用样式
                for (int i = 0; i < 8; i++) {
                    row.getCell(i).setCellStyle(dataStyle);
                }
            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                if (sheet.getColumnWidth(i) > 8000) {
                    sheet.setColumnWidth(i, 8000);
                }
            }

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=attendance_records_" + LocalDate.now() + ".xlsx");

            // 写入响应
            workbook.write(response.getOutputStream());
        }
    }

    // ==================== REST API 方法 ====================

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @ResponseBody
    public Result<Attendance> addAttendance(@RequestBody Attendance attendance) {
        Attendance saved = attendanceService.addAttendance(attendance);
        return Result.success(saved);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER') or (#studentId == authentication.principal.id)")
    @ResponseBody
    public Result<List<Attendance>> getByStudentId(@PathVariable Long studentId) {
        return Result.success(attendanceService.getAttendanceByStudentId(studentId));
    }

    @GetMapping("/date")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @ResponseBody
    public Result<List<Attendance>> getByDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return Result.success(attendanceService.getAttendanceByDate(date));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @ResponseBody
    public Result<List<Attendance>> getAll() {
        return Result.success(attendanceService.getAllAttendances());
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @ResponseBody
    public Result<Attendance> updateAttendance(@RequestBody Attendance attendance) {
        Attendance updated = attendanceService.updateAttendance(attendance);
        return Result.success(updated);
    }

    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @ResponseBody
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
    @ResponseBody
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

    // ==================== 辅助方法 ====================

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "anonymous";
    }

    private boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_" + role));
    }
}