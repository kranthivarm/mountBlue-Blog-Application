package com.example.demo.models;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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

//    private List<TagModel> tags;
    private String tags;
}
