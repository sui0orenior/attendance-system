package com.example.attendancesystem.entity;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;           // 课程名称，如 "Java程序设计"

    @Column(name = "class_name", length = 50)
    private String className;      // 班级名称，如 "2023级软件工程1班"

    @Column(name = "start_time")
    private LocalTime startTime;   // 上课开始时间，如 08:00:00

    @Column(name = "end_time")
    private LocalTime endTime;     // 上课结束时间，如 10:00:00

    @Column(name = "teacher_id")
    private Long teacherId;        // 授课教师ID

    // 无参构造
    public Course() {}

    // 全参构造（可选）
    public Course(Long id, String name, String className, LocalTime startTime, LocalTime endTime, Long teacherId) {
        this.id = id;
        this.name = name;
        this.className = className;
        this.startTime = startTime;
        this.endTime = endTime;
        this.teacherId = teacherId;
    }

    // ========== Getter 和 Setter ==========
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
}