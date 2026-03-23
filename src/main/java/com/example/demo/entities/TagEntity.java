package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "tags",
        indexes = {
                @Index(name="index_tag_name",columnList = "name",unique = true)
        }
)
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class TagEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String name;

    @CreatedDate
    @Column(nullable = false , updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "tags",fetch = FetchType.LAZY)
    @ToString.Exclude// circurlar reference
    private List<PostEntity>posts;
}
