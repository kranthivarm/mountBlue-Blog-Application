package com.example.demo.repository;

import com.example.demo.entities.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity,Integer> {
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
      And (:authorName is null or :authorName = '' or p.author = :authorName)
      And (:skipTagFilter = true OR t.name IN :tagNames)
      And p.publishedAt>= :startDate
      And p.publishedAt<= :endDate
      """)
    public Page<PostEntity> findFilteredPosts(
            @Param("search") String search,
            @Param("authorName") String authorName,
            @Param("tagNames") List<String> tagNames,
            @Param("skipTagFilter") boolean skipTagFilter,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("select DISTINCT p from PostEntity p Left Join FETCH p.tags")
    public List<PostEntity>findAllWithTags();

    @Query("select distinct p from PostEntity p left JOIN Fetch p.tags where p.id=:id")
    public Optional<PostEntity> findByIdWithTags(@Param("id") int id);

    @Query("select distinct p.author from PostEntity p where p.author is not null")
    public List<String> findAllDistinctAuthors();
    @Query("select distinct t.name from TagEntity t")
    public List<String> finAllDistinctTags();
}
