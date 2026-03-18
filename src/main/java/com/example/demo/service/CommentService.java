package com.example.demo.service;

import com.example.demo.Utils.EntityToDtoConvertorViceVersa;
import com.example.demo.dtos.CommentDto;
import com.example.demo.entities.CommentsEntity;
import com.example.demo.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public List<CommentDto> findCommentsByPostId(int postId) {
        List<CommentsEntity> commentsEntities =
                commentRepository.findByPost_Id(postId);
        List<CommentDto> commentDtos = new ArrayList<>();
        for(CommentsEntity commentsEntity : commentsEntities) {
            commentDtos.add(
               entityToDtoConvertorViceVersa.commentEntityToDto(commentsEntity)
            );
        }
        return commentDtos;
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