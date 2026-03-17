package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CommentsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;
    private  String name;
    private  String email;
    private  String comment;
    //foreign key
    @ManyToOne
    @JoinColumn(name="postId", updatable = false)
    @JsonIgnore //infinite recursion
    private PostEntity post;
//    private  int post_id;

    @CreatedDate
    @Column(nullable = false , updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
