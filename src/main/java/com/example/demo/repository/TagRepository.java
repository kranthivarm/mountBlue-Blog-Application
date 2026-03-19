package com.example.demo.repository;

import com.example.demo.entities.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<TagEntity,Integer> {
    TagEntity findByName(String name);

    @Query("select t.name from TagEntity t")
    List<String> findAllTagNames();
}
