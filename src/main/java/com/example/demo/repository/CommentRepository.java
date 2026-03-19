package com.example.demo.repository;

import com.example.demo.entities.CommentsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentsEntity,Integer> {
    //postId isn't direct field in commentEntt
    List<CommentsEntity> findByPost_Id(int postId);

    Page<CommentsEntity> findByPost_Id(int postId, Pageable pageable);
}
