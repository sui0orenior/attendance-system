package com.example.attendancesystem.specification;


import com.example.attendancesystem.entity.Attendance;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;


public class AttendanceSpecifications {

    public static Specification<Attendance> hasStudentNumber(String studentNumber) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(studentNumber)) {
                return cb.conjunction();
            }
            Join<Object, Object> studentJoin = root.join("student", JoinType.LEFT);
            return cb.equal(studentJoin.get("studentNumber"), studentNumber);
        };
    }

    public static Specification<Attendance> hasStatus(String status) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(status)) {
                return cb.conjunction();
            }
            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<Attendance> hasDateBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            if (startDate == null && endDate == null) {
                return cb.conjunction();
            }
            if (startDate != null && endDate != null) {
                return cb.between(root.get("attendanceDate"), startDate, endDate);
            } else if (startDate != null) {
                return cb.greaterThanOrEqualTo(root.get("attendanceDate"), startDate);
            } else {
                return cb.lessThanOrEqualTo(root.get("attendanceDate"), endDate);
            }
        };
    }
}