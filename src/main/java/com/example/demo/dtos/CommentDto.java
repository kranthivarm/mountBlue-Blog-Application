package com.example.demo.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private  int id;
    private  String name;
    private  String email;
    private  String comment;

    private int postId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
