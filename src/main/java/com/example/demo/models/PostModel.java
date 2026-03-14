package com.example.demo.models;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostModel {
    private int id;
    private String title;
    private String excerpt;
    private String content;
    private String author;
    private LocalDateTime publishedAt;
    private boolean isPublished;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
