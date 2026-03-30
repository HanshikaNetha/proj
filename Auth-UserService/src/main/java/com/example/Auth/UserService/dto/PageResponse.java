package com.example.Auth.UserService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageResponse<T>{
    private List<T> ListContent;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
