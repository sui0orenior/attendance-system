package com.example.attendancesystem.util;

import org.springframework.data.domain.Sort;

public class SortUtils {
    public static Sort createSort(String sortBy, String sortOrder) {
        if (sortBy == null || sortBy.isEmpty()) {
            return Sort.unsorted();
        }
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, sortBy);
    }
}