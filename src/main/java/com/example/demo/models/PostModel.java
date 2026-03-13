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
    private LocalDateTime published_at;
    private boolean is_published;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
