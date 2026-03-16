package com.example.demo.dtos;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class TagDto {
    private int id;
    private String name;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

//    private List<PostModel> posts;
}
