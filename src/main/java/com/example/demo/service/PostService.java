package com.example.demo.service;

import com.example.demo.Utils.EntityToDtoConvertorViceVersa;
import com.example.demo.entities.PostEntity;
import com.example.demo.dtos.PostDto;
import com.example.demo.entities.TagEntity;
import com.example.demo.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
        PostEntity newPostEntity = entityToDtoConvertorViceVersa.postDtoToEntityCreate(postDto);
        //inserting entity
        PostEntity insertedEntity=postRepository.save(newPostEntity);
        //return model again;
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

    public List<PostDto> getFilteredPosts(
            String search, String authorName,
            List<String>tagNames,
            String sortField,String order
    ){
        System.out.println("all blogsService"+search+authorName);
        List<PostEntity>postEntities;
        if((search==null || search.isEmpty()) &&
        (authorName==null || authorName.isEmpty()) &&
        (tagNames == null || tagNames.isEmpty())){
            if(order.equalsIgnoreCase("asc"))
                postEntities=postRepository.findAllByOrderByPublishedAtAsc();
            else postEntities=postRepository.findAllByOrderByPublishedAtDesc();
        }
        else {
            Sort sort = order.equalsIgnoreCase("asc") ?
                    Sort.by(sortField).ascending() :
                    Sort.by(sortField).descending();
//            if(tagNames==null || tagNames.isEmpty())tagNames=null;

            boolean skipTagFilter = (tagNames == null || tagNames.isEmpty());
            List<String> safeTagNames = skipTagFilter ? List.of("__dummy__") : tagNames;
            postEntities = postRepository.findFilteredPosts(
                    search, authorName, tagNames, skipTagFilter,sort
            );
        }
        System.out.println("postentities"+postEntities.size());
        List<PostDto> postDtos=entityToDtoConvertorViceVersa.postEntityToDto(postEntities);
        System.out.print("dtos" + postDtos.size());
        for(PostDto postDto:postDtos) System.out.println(postDto.getId());
        return postDtos;
    }
}
