package com.example.demo.service;

import com.example.demo.Utils.EntityToDtoConvertorViceVersa;
import com.example.demo.entities.PostEntity;
import com.example.demo.dtos.PostDto;
import com.example.demo.entities.TagEntity;
import com.example.demo.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    public  List<PostDto> findAllOrderByPublishedAt(String order,int page,int pageSize){
        List<PostEntity>postEntities;
        Pageable pageable= PageRequest.of(page,pageSize);
        if(order.equals("Asc"))postEntities=postRepository.findAllByOrderByPublishedAtAsc(pageable).getContent();
        else postEntities=postRepository.findAllByOrderByPublishedAtDesc(pageable).getContent();
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

    public Map<String,Object> getFilteredPosts(
            String search, String authorName,
            List<String>tagNames,
            String sortField,String order,
            int page,int pageSize
    ){
        System.out.println("all blogsService"+search+authorName);

        Sort sort = order.equalsIgnoreCase("asc") ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable=PageRequest.of(page,pageSize,sort);
        Page<PostEntity>pageResultsPostEntities;

        List<PostEntity>postEntities;
        if((search==null || search.isEmpty()) &&
        (authorName==null || authorName.isEmpty()) &&
        (tagNames == null || tagNames.isEmpty())){
            if(order.equalsIgnoreCase("asc"))
                pageResultsPostEntities=postRepository.findAllByOrderByPublishedAtAsc(pageable);
            else pageResultsPostEntities=postRepository.findAllByOrderByPublishedAtDesc(pageable);
        }
        else {
//            if(tagNames==null || tagNames.isEmpty())tagNames=null;

            boolean skipTagFilter = (tagNames == null || tagNames.isEmpty());
            List<String> safeTagNames = skipTagFilter ? List.of("__dummy__") : tagNames;
            pageResultsPostEntities = postRepository.findFilteredPosts(
//                    search, authorName, safeTagNames, skipTagFilter,sort
                      search, authorName, safeTagNames, skipTagFilter,pageable
            );
        }
        List<PostDto> postDtos = entityToDtoConvertorViceVersa
                .postEntityToDto(pageResultsPostEntities.getContent());

        // Return both the posts and pagination metadata
        Map<String, Object> result = new HashMap<>();
        result.put("posts", postDtos);
        result.put("currentPage", pageResultsPostEntities.getNumber());
        result.put("totalPages", pageResultsPostEntities.getTotalPages());
        result.put("totalItems", pageResultsPostEntities.getTotalElements());
        return result;
    }
}
