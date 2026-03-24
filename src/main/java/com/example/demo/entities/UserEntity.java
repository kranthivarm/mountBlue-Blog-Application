package com.example.demo.entities;

import com.example.demo.enums.Role;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false,unique = true)
    private String email;
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role=Role.USER;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<PostEntity>post=new ArrayList<>();

    @CreatedDate
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(nullable = false)
    private  LocalDateTime updatedAt;
}
