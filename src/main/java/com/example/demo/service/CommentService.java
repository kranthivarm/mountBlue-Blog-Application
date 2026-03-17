package com.example.demo.service;

import com.example.demo.Utils.EntityToDtoConvertor;
import com.example.demo.dtos.CommentDto;
import com.example.demo.entities.CommentsEntity;
import com.example.demo.entities.PostEntity;
import com.example.demo.repository.CommentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final EntityToDtoConvertor entityToDtoConvertor;

    @Autowired
    CommentService(CommentRepository commentRepository,  EntityToDtoConvertor entityToDtoConvertor) {
        this.commentRepository = commentRepository;
        this.entityToDtoConvertor=entityToDtoConvertor;
    }
    public void createComment(CommentDto commentDto) {
        commentRepository.save(
             entityToDtoConvertor.commentsDtoToEntity(commentDto)
        );
    }

    public List<CommentDto> findCommentsByPostId(int postId) {
        List<CommentsEntity> commentsEntities =
                commentRepository.findByPost_Id(postId);
        List<CommentDto> commentDtos = new ArrayList<>();
        for (CommentsEntity commentsEntity : commentsEntities) {
            commentDtos.add(
               entityToDtoConvertor.commentEntityToDto(commentsEntity)
            );
        }
        return commentDtos;
    }

    public CommentDto findByCommentId(int id){
        return
           entityToDtoConvertor.commentEntityToDto(
             commentRepository.findById(id).orElse(null)
           );
    }

    public void deleteCommentBycommentId(int id){
        commentRepository.deleteById(id);
    }

    public void updateComment(CommentDto commentDto){
        commentRepository.save(entityToDtoConvertor.commentsDtoToEntity(commentDto));
    }

}