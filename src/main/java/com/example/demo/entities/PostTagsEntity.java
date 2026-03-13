package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_tags")
@Data
@NoArgsConstructor
public class PostTagsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int post_id;
    private  int tag_id;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
