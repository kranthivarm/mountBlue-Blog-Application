package com.example.demo.service;

import com.example.demo.Utils.EntityToDtoConvertor;
import com.example.demo.dtos.CommentDto;
import com.example.demo.entities.PostEntity;
import com.example.demo.entities.TagEntity;
import com.example.demo.dtos.PostDto;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.TagRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final EntityToDtoConvertor entityToDtoConvertor;

    @Autowired
    public PostService(
        PostRepository postRepository,
        EntityToDtoConvertor entityToDtoConvertor
    ){
        this.postRepository=postRepository;
        this.entityToDtoConvertor=entityToDtoConvertor;
    }


    public List<PostDto> findAll(){
        List<PostEntity>postEntities=postRepository.findAll();
        List<PostDto>postDtos=new ArrayList<>();
        for(PostEntity entity: postEntities){
            postDtos.add(
                 entityToDtoConvertor.postEntityToDto(entity)
//                modelMapper.map(entity, PostDto.class)
            );
        }
        return postDtos;
    }

    public PostDto findById(int id) {
//        return modelMapper.map(postRepository.findById(id),PostModel.class);
        PostEntity postEntity=postRepository.findById(id).orElse(null);
        if(postEntity==null)return null;
//        PostDto postDto =modelMapper.map(postEntity, PostDto.class);
//
////        String tagString=String.join(',',postEntity.getTags());
//        StringBuilder tagStr=new StringBuilder("");
//        for(TagEntity tagEntity: postEntity.getTags()){
//            tagStr.append(tagEntity.getName()+",");
//        }
//        postDto.setTags(tagStr.toString());
        return entityToDtoConvertor.postEntityToDto(postEntity);
    }
    @Transactional
    public PostDto createNewPost(PostDto postDto){
        PostEntity newPostEntity =entityToDtoConvertor.postDtoToEntity(postDto);
        //inserting entity
        PostEntity insertedEntity=postRepository.save(newPostEntity);
        //return model again;
//        return modelMapper.map(insertedEntity, PostDto.class);
        return  entityToDtoConvertor.postEntityToDto(insertedEntity);
    }

    public  List<PostDto> findAllOrderByPublishedAt(String order){
        List<PostEntity>postEntities;
        if(order.equals("Asc"))postEntities=postRepository.findAllByOrderByPublishedAtAsc();
        else postEntities=postRepository.findAllByOrderByPublishedAtDesc();
        List<PostDto>posts=new ArrayList<>();
        for(PostEntity entity: postEntities) {
            posts.add(
//                 modelMapper.map(entity, PostDto.class)
                    entityToDtoConvertor.postEntityToDto(entity)
            );
        }
        return posts;
    }

    public void deleteById(int id){
        postRepository.deleteById(id);
    }

    public void updatePost(PostDto postDto){
        System.out.println("updatePost Service");
        postRepository.save(
//            modelMapper.map(postDto,PostEntity.class)
              entityToDtoConvertor.postDtoToEntity(postDto)
        );
    }
}
