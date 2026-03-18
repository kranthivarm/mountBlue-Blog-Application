package com.example.demo.repository;

import com.example.demo.entities.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity,Integer> {
//    public List<PostEntity> findAllByOrderByPublishedAtAsc();
//    public List<PostEntity> findAllByOrderByPublishedAtDesc();
public Page<PostEntity> findAllByOrderByPublishedAtAsc(Pageable pageable);
public Page<PostEntity> findAllByOrderByPublishedAtDesc(Pageable pageable);
    @Query("""
      select distinct p from PostEntity p
      Left join p.tags t
      where
          (:search is null or :search = '' or
            LOWER(p.title)   LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(p.content) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(p.excerpt) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(p.author)  LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(t.name)    LIKE LOWER(CONCAT('%', :search, '%'))
          )
      AND (:authorName is null or :authorName = '' or p.author = :authorName)
      And (:skipTagFilter = true OR t.name IN :tagNames)
      """)
    public Page<PostEntity> findFilteredPosts(
            @Param("search") String search,
            @Param("authorName") String authorName,
            @Param("tagNames") List<String> tagNames,
            @Param("skipTagFilter") boolean skipTagFilter,
//            Sort sort
            Pageable pageable
    );
}
