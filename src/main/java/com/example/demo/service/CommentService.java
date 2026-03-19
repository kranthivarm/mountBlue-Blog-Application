package com.example.demo.service;

import com.example.demo.Utils.EntityToDtoConvertorViceVersa;
import com.example.demo.dtos.CommentDto;
import com.example.demo.dtos.PagedResult;
import com.example.demo.entities.CommentsEntity;
import com.example.demo.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final EntityToDtoConvertorViceVersa entityToDtoConvertorViceVersa;
    @Autowired
    CommentService(CommentRepository commentRepository,  EntityToDtoConvertorViceVersa entityToDtoConvertorViceVersa) {
        this.commentRepository = commentRepository;
        this.entityToDtoConvertorViceVersa = entityToDtoConvertorViceVersa;
    }

    public void createComment(CommentDto commentDto) {
        commentRepository.save(
             entityToDtoConvertorViceVersa.commentsDtoToEntity(commentDto)
        );
    }

    public PagedResult<CommentDto> findCommentsByPostId(int postId, int page, int pageSize) {
        Pageable pageable= PageRequest.of(page,pageSize,Sort.by("createdAt").descending());
        Page<CommentsEntity> pageCommentsEntities =
                commentRepository.findByPost_Id(postId,pageable);
        List<CommentDto> commentDtos = new ArrayList<>();
        for(CommentsEntity commentsEntity : pageCommentsEntities.getContent()) {
            commentDtos.add(
               entityToDtoConvertorViceVersa.commentEntityToDto(commentsEntity)
            );
        }
        return new PagedResult<>(
                commentDtos,
                pageCommentsEntities.getNumber(),
                pageCommentsEntities.getTotalPages(),
                pageCommentsEntities.getTotalElements()
        );
    }

    public CommentDto findByCommentId(int id){
        return
           entityToDtoConvertorViceVersa.commentEntityToDto(
             commentRepository.findById(id).orElse(null)
           );
    }

    public void deleteCommentBycommentId(int id){
        commentRepository.deleteById(id);
    }

    public void updateComment(CommentDto commentDto){
//        commentRepository.save(entityToDtoConvertorViceVersa.commentsDtoToEntity(commentDto));
        CommentsEntity existingCommentEntity=commentRepository.findById(commentDto.getId())
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        existingCommentEntity.setName(commentDto.getName());
        existingCommentEntity.setEmail(commentDto.getEmail());
        existingCommentEntity.setComment(commentDto.getComment());

        commentRepository.save(existingCommentEntity);
    }

}