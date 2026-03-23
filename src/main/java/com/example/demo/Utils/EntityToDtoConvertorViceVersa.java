package com.example.demo.Utils;

import com.example.demo.dtos.CommentDto;
import com.example.demo.dtos.PostDto;
import com.example.demo.entities.CommentsEntity;
import com.example.demo.entities.PostEntity;
import com.example.demo.entities.TagEntity;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.TagRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Component
public class EntityToDtoConvertorViceVersa {
    private  final ModelMapper modelMapper;
    private  final TagRepository tagRepository;
    private final PostRepository postRepository;

    @Autowired
    EntityToDtoConvertorViceVersa(ModelMapper mapper, TagRepository tagRepository,PostRepository postRepository){
        this.modelMapper=mapper;
        this.tagRepository=tagRepository;
        this.postRepository=postRepository;
    }
    @Transactional
    public Set<TagEntity> tagDtosToEntities(String tagsString){
        Set<TagEntity> tags = new HashSet<>();
        if(tagsString == null || tagsString.trim().isEmpty()){
            return tags;
        }
        Set<String> tagNames = Arrays.stream(tagsString.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toSet());

        for(String tag : tagNames){
            TagEntity tagEntity = tagRepository.findByName(tag);
            if(tagEntity == null){
                tagEntity = new TagEntity();
                tagEntity.setName(tag);
                tagRepository.save(tagEntity);
            }
            tags.add(tagEntity);
        }
        return tags;
    }

    @Transactional(readOnly = true)
    public PostDto postEntityToDto(PostEntity postEntity){
        PostDto postDto=modelMapper.map(postEntity,PostDto.class);
        //tags
        StringBuilder tags=new StringBuilder();
        for(TagEntity tagEntity:postEntity.getTags()){
            if(tagEntity!=null)tags.append(tagEntity.getName()).append(",");
        }
        if(!tags.isEmpty())tags.deleteCharAt(tags.length() - 1);
        postDto.setTags(tags.toString());

        //commented because of pageNation
        //comments
//        List<CommentDto> commentDtos=
//                postEntity.getComments()
//                   .stream()
//                   .map(
//                      commentsEntity -> {
//                         return commentEntityToDto(commentsEntity);
//                      }
//                   )
//                   . toList();
//        postDto.setComments(commentDtos);
        postDto.setComments(new ArrayList<>());
        return postDto;
    }

    public List<PostDto> postEntityToDto(List<PostEntity> postEntities){
        List<PostDto>postDtos=new ArrayList<>();
        for(PostEntity postEntity:postEntities){
            postDtos.add(postEntityToDto(postEntity));
        }
        return postDtos;
    }

    public PostEntity postDtoToEntityCreate(PostDto postDto){
        PostEntity newPostEntity=modelMapper.map(postDto,PostEntity.class);
        newPostEntity.setTags(tagDtosToEntities(postDto.getTags()));
        List<CommentsEntity>commentsEntities=new ArrayList<>();
        newPostEntity.setComments(commentsEntities);
        return newPostEntity;
    }
    @Transactional
    public PostEntity postDtoToEntity(PostDto postDto){
        //converting model to entity without Tags

//        PostEntity existingPostEntity = postRepository.findById(postDto.getId())
//                .orElseThrow(() -> new RuntimeException("Post not found"));
        PostEntity existingPostEntity=postRepository.findByIdWithTags(postDto.getId())
                .orElseThrow(()->new RuntimeException("Post Not found"));
        //comments already came from db;
        existingPostEntity.setTitle(postDto.getTitle());
        existingPostEntity.setExcerpt(postDto.getExcerpt());
        existingPostEntity.setContent(postDto.getContent());
        existingPostEntity.setAuthor(postDto.getAuthor());

        Set<TagEntity> tags = tagDtosToEntities(postDto.getTags());
        if(tags != null){
            existingPostEntity.getTags().clear();
            existingPostEntity.getTags().addAll(tags);
        }

//        //comments;
//        List<CommentsEntity> commentsEntities=new ArrayList<>();
//        if(postDto.getComments()!=null){
//            for(CommentDto commentDto:postDto.getComments()){
//                commentsEntities.add(
//                   commentsDtoToEntity(commentDto)
//                );
//            }
//        }
        return existingPostEntity;
    }

    public CommentDto commentEntityToDto(CommentsEntity commentsEntity) {
        if(commentsEntity==null)return null;
        CommentDto commentDto=modelMapper.map(commentsEntity, CommentDto.class);
        if(commentsEntity.getPost()!=null){
            commentDto.setPostId(commentsEntity.getPost().getId());
        }
        return commentDto;
    }

    public CommentsEntity commentsDtoToEntity(CommentDto commentDto) {
        if(commentDto==null)return  null;
        CommentsEntity commentsEntity= modelMapper.map(commentDto, CommentsEntity.class);
        ////This will hit Db;
//         PostEntity postEntity=postRepository.findById(commentDto.getPostId()).orElseThrow(()-> new RuntimeException("No post found"));

        //  gets a proxy reference JPA recognizes
        PostEntity postEntity = postRepository.getReferenceById(commentDto.getPostId());
        commentsEntity.setPost(postEntity);
        return commentsEntity;
    }
}