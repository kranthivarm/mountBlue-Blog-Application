package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
public class CommentsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;
    private  String name;
    private  String email;
    private  String comment;
    //foreign key
    private  int post_id;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
