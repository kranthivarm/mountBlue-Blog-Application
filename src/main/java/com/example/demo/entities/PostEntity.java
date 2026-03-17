package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String excerpt;
    private String content;
    private String author;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime publishedAt;
    private boolean isPublished;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToMany
    @JoinTable(
         name="PostTags",
         joinColumns = @JoinColumn(name="postId"),
         inverseJoinColumns =@JoinColumn(name="tagId")
    )
    private Set<TagEntity> tags=new HashSet<>();

    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL)
    private List<CommentsEntity> comments=new ArrayList<>();

    @PrePersist
    public void prePersist(){
        this.isPublished =true;
//        this.publishedAt =LocalDateTime.now();
    }
}
