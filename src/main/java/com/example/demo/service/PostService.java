package com.example.demo.service;

import com.example.demo.Utils.EntityToDtoConvertorViceVersa;
import com.example.demo.entities.PostEntity;
import com.example.demo.dtos.PostDto;
import com.example.demo.entities.TagEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final EntityToDtoConvertorViceVersa entityToDtoConvertorViceVersa;
    private final UserRepository userRepository;

    public List<PostDto> findAll(){
//        List<PostEntity>postEntities=postRepository.findAll();
        List<PostEntity>postEntities=postRepository.findAllWithTags();
        return entityToDtoConvertorViceVersa.postEntityToDto(postEntities);
    }

    public PostDto findById(int id) {
//        PostEntity postEntity=postRepository.findById(id).orElse(null);
        PostEntity postEntity=postRepository.findByIdWithTags(id).orElse(null);
        if(postEntity==null)return null;
        return entityToDtoConvertorViceVersa.postEntityToDto(postEntity);
    }
//    public PostDto findByIdWithTags(int id) {
////        PostEntity postEntity=postRepository.findById(id).orElse(null);
//        PostEntity postEntity=postRepository.findByIdWithTags(id).orElse(null);
//        if(postEntity==null)return null;
//        return entityToDtoConvertorViceVersa.postEntityToDto(postEntity);
//    }

    @Transactional
    public PostDto createNewPost(PostDto postDto){
        PostEntity newPostEntity = entityToDtoConvertorViceVersa.postDtoToEntityCreate(postDto);

        String authorEmail=
                (postDto.getAuthor()==null ||postDto.getAuthor().isEmpty())
                ?"":postDto.getAuthor();

        Optional<UserEntity>userEntity=userRepository.findByEmail(postDto.getAuthor());
        //seting user to post
        if(userEntity.isPresent())newPostEntity.setUser(userEntity.get());
        else throw  new RuntimeException("No User Found for this Post");

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

        PostEntity existingPostEntity=entityToDtoConvertorViceVersa.postDtoToEntity(postDto);
        postRepository.save(existingPostEntity);
    }

    public Map<String,Object> getFilteredPosts(
            String search, String authorName,
            List<String>tagNames,
            String sortField,String order,
            LocalDate startDate,LocalDate endDate,
            int page,int pageSize
    ){
        System.out.println("all blogsService"+search+authorName);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime   = endDate.atTime(23, 59, 59);

        Sort sort = order.equalsIgnoreCase("asc") ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable=PageRequest.of(page,pageSize,sort);
        Page<PostEntity>pageResultsPostEntities;
            boolean skipTagFilter = (tagNames == null || tagNames.isEmpty());
            List<String> safeTagNames = skipTagFilter ? List.of("__dummy__") : tagNames;

//            boolean noFilters=(search==null || search.isEmpty())
//                    &&(authorName==null || authorName.isEmpty())
//                    && skipTagFilter;

            pageResultsPostEntities = postRepository.findFilteredPosts(
                      search, authorName, safeTagNames, skipTagFilter,
                    startDateTime,endDateTime,pageable
            );
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

    public List<String> getAllAuthors(){
        return postRepository.findAllDistinctAuthors();

    }
    public List<String> getAllTags(){
        return postRepository.finAllDistinctTags();
    }
}
