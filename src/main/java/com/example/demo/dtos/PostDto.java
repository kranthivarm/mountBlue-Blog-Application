package com.example.demo.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class PostDto {
    private int id;
    private String title;
    private String excerpt;
    private String content;
    private String author;
    private LocalDateTime publishedAt;
    private boolean isPublished;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentDto>comments;
//    private Set<TagDto> tags;
    private String tags;
}
