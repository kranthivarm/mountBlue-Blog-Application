package com.example.demo.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PagedResult<T> {
    private List<T> content;
    private int currentPage;
    private int totalPages;
    private long totalItems;
}
