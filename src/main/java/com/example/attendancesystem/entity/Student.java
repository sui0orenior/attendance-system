package com.example.attendancesystem.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "student_no", nullable = false, unique = true, length = 20)
    private String studentNo;

    @Column(name = "class_name", length = 50)
    private String className;

    @Column(length = 10)
    private String gender;

    private LocalDate birthday;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    @Column(length = 200)
    private String address;

    private String remark;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    // 无参构造
    public Student() {}

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getStudentNo() { return studentNo; }
    public String getClassName() { return className; }
    public String getGender() { return gender; }
    public LocalDate getBirthday() { return birthday; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getRemark() { return remark; }
    public LocalDateTime getCreateTime() { return createTime; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setStudentNo(String studentNo) { this.studentNo = studentNo; }
    public void setClassName(String className) { this.className = className; }
    public void setGender(String gender) { this.gender = gender; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setAddress(String address) { this.address = address; }
    public void setRemark(String remark) { this.remark = remark; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}