package com.example.demo.repository;

import com.example.demo.entities.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity,Integer> {
    public List<PostEntity> findAllByOrderByPublishedAtAsc();
}
