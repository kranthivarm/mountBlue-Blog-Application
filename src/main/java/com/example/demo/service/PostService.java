package com.example.demo.service;

import com.example.demo.Utils.EntityToDtoConvertorViceVersa;
import com.example.demo.entities.PostEntity;
import com.example.demo.dtos.PostDto;
import com.example.demo.entities.TagEntity;
import com.example.demo.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final EntityToDtoConvertorViceVersa entityToDtoConvertorViceVersa;

    @Autowired
    public PostService(
        PostRepository postRepository,
        EntityToDtoConvertorViceVersa entityToDtoConvertorViceVersa
    ){
        this.postRepository=postRepository;
        this.entityToDtoConvertorViceVersa = entityToDtoConvertorViceVersa;
    }


    public List<PostDto> findAll(){
        List<PostEntity>postEntities=postRepository.findAll();
        List<PostDto>postDtos=new ArrayList<>();
        for(PostEntity entity: postEntities){
            postDtos.add(
                 entityToDtoConvertorViceVersa.postEntityToDto(entity)
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
        return entityToDtoConvertorViceVersa.postEntityToDto(postEntity);
    }
    @Transactional
    public PostDto createNewPost(PostDto postDto){
        PostEntity newPostEntity = entityToDtoConvertorViceVersa.postDtoToEntity(postDto);
        //inserting entity
        PostEntity insertedEntity=postRepository.save(newPostEntity);
        //return model again;
//        return modelMapper.map(insertedEntity, PostDto.class);
        return  entityToDtoConvertorViceVersa.postEntityToDto(insertedEntity);
    }

    public  List<PostDto> findAllOrderByPublishedAt(String order){
        List<PostEntity>postEntities;
        if(order.equals("Asc"))postEntities=postRepository.findAllByOrderByPublishedAtAsc();
        else postEntities=postRepository.findAllByOrderByPublishedAtDesc();
        List<PostDto>posts=new ArrayList<>();
        for(PostEntity entity: postEntities) {
            posts.add(
//                 modelMapper.map(entity, PostDto.class)
                    entityToDtoConvertorViceVersa.postEntityToDto(entity)
            );
        }
        return posts;
    }

    public void deleteById(int id){
        postRepository.deleteById(id);
    }

    public void updatePost(PostDto postDto){

//        PostEntity existing = postRepository.findById(postDto.getId())
//                .orElseThrow(() -> new RuntimeException("Post not found"));
//        existing.setTitle(postDto.getTitle());
//        existing.setExcerpt(postDto.getExcerpt());
//        existing.setContent(postDto.getContent());
//        existing.setAuthor(postDto.getAuthor());
//
//        Set<TagEntity> tags = entityToDtoConvertorViceVersa.tagDtosToEntities(postDto.getTags());
//        if(tags != null){
//            existing.getTags().clear();
//            existing.getTags().addAll(tags);
//        }
        PostEntity existingPostEntity=entityToDtoConvertorViceVersa.postDtoToEntity(postDto);

        postRepository.save(existingPostEntity);
    }
}
