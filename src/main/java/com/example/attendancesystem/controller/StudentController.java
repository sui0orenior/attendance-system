package com.example.attendancesystem.controller;

import com.example.attendancesystem.entity.Student;
import com.example.attendancesystem.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller  // 注意：是 @Controller，不是 @RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    /**
     * 学生列表页面（支持分页、搜索、排序）
     */
    @GetMapping("/list")
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) String keyword,
            Model model) {

        // 查询分页数据
        Page<Student> pageData = studentService.findAll(page, size, sortBy, sortOrder, keyword);

        // 传递给前端
        model.addAttribute("students", pageData.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageData.getTotalPages());
        model.addAttribute("totalElements", pageData.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);

        return "student-list";
    }

    /**
     * 新增学生页面
     */
    @GetMapping("/add")
    public String addPage(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("title", "新增学生");
        return "student-form";
    }

    /**
     * 编辑学生页面（数据回显）
     */
    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable Long id, Model model) {
        Student student = studentService.findById(id);
        model.addAttribute("student", student);
        model.addAttribute("title", "编辑学生");
        return "student-form";
    }

    /**
     * 保存学生（新增或编辑）
     */
    @PostMapping("/save")
    public String save(@ModelAttribute Student student, RedirectAttributes redirectAttributes) {
        try {
            studentService.save(student);
            redirectAttributes.addFlashAttribute("successMsg", "保存成功！");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "保存失败：" + e.getMessage());
        }
        return "redirect:/student/list";
    }

    /**
     * 删除单个学生
     */
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            studentService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMsg", "删除成功！");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "删除失败：" + e.getMessage());
        }
        return "redirect:/student/list";
    }

    /**
     * 批量删除学生
     */
    @GetMapping("/batchDelete")
    public String batchDelete(@RequestParam String ids, RedirectAttributes redirectAttributes) {
        try {
            String[] idArray = ids.split(",");
            int count = 0;
            for (String id : idArray) {
                studentService.deleteById(Long.parseLong(id));
                count++;
            }
            redirectAttributes.addFlashAttribute("successMsg", "成功删除 " + count + " 条数据");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "批量删除失败：" + e.getMessage());
        }
        return "redirect:/student/list";
    }
}