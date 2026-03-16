package com.example.demo.service;

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
    private final TagRepository tagRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PostService(
            PostRepository postRepository,
            TagRepository tagRepository,
            ModelMapper modelMapper
    ){
        this.postRepository=postRepository;
        this.tagRepository=tagRepository;
        this.modelMapper=modelMapper;
    }
    public PostDto postEntityToDto(PostEntity postEntity){
        PostDto postDto=modelMapper.map(postEntity,PostDto.class);
        StringBuilder tags=new StringBuilder("");
        for(TagEntity tagEntity:postEntity.getTags()){
            if(tagEntity!=null)tags.append(tagEntity.getName()+",");
        }
//        tags.deleteCharAt()//delete LastChar;
        postDto.setTags(tags.toString());
        return postDto;
    }
    public PostEntity postDtoToEntity(PostDto postDto){
        //converting model to entity without Tags
        PostEntity newPostEntity =modelMapper.map(postDto,PostEntity.class);
//        String [] tags= postDto.getTags().split(",");
        HashSet<String>tagsNameSet=
                new HashSet<>(
                        Arrays.asList(
//                          tags
                            postDto.getTags().split(",")
                        )
                );
        HashSet<TagEntity> tagsList=new HashSet<>();
        for(String tagWithSpace:tagsNameSet){
            String tag=tagWithSpace.trim();
            if(tag.isEmpty())continue;// to check spaces also
            TagEntity tagEntity = tagRepository.findByName(tag);
            if(tagEntity==null){
                tagEntity=new TagEntity();
                tagEntity.setName(tag);
                tagRepository.save(tagEntity);
            }
            tagsList.add(tagEntity);
        }
        newPostEntity.setTags(tagsList);
        return newPostEntity;
    }

    public List<PostDto> findAll(){
        List<PostEntity>postEntities=postRepository.findAll();
        List<PostDto>postDtos=new ArrayList<>();
        for(PostEntity entity: postEntities){
            postDtos.add(
                 postEntityToDto(entity)
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
        PostDto postDto=postEntityToDto(postEntity);
        return postDto;
    }
    @Transactional
    public PostDto createNewPost(PostDto postDto){
        PostEntity newPostEntity =postDtoToEntity(postDto);
        //inserting entity
        PostEntity insertedEntity=postRepository.save(newPostEntity);
        //return model again;
        return modelMapper.map(insertedEntity, PostDto.class);
    }

    public  List<PostDto> findAllOrderByPublishedAt(String order){
        List<PostEntity>postEntities;
        if(order.equals("Asc"))postEntities=postRepository.findAllByOrderByPublishedAtAsc();
        else postEntities=postRepository.findAllByOrderByPublishedAtDesc();
        List<PostDto>posts=new ArrayList<>();
        for(PostEntity entity: postEntities) {
            posts.add(
                 modelMapper.map(entity, PostDto.class)
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
              postDtoToEntity(postDto)
        );
    }
}
