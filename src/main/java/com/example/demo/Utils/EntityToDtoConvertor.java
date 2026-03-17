package com.example.demo.Utils;

import com.example.demo.dtos.CommentDto;
import com.example.demo.dtos.PostDto;
import com.example.demo.entities.CommentsEntity;
import com.example.demo.entities.PostEntity;
import com.example.demo.entities.TagEntity;
import com.example.demo.repository.TagRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


@Component
public class EntityToDtoConvertor {
    private  final ModelMapper modelMapper;
    private  final TagRepository tagRepository;

    @Autowired
    EntityToDtoConvertor(ModelMapper mapper, TagRepository tagRepository){
        this.modelMapper=mapper;
        this.tagRepository=tagRepository;
    }

    public PostDto postEntityToDto(PostEntity postEntity){
        PostDto postDto=modelMapper.map(postEntity,PostDto.class);
        //tags
        StringBuilder tags=new StringBuilder();
        for(TagEntity tagEntity:postEntity.getTags()){
            if(tagEntity!=null)tags.append(tagEntity.getName()).append(",");
        }
        if(!tags.isEmpty())tags.deleteCharAt(tags.length() - 1);
        postDto.setTags(tags.toString());

        //comments
        List<CommentDto> commentDtos=
                postEntity.getComments()
                   .stream()
                   .map(
                      commentsEntity -> {
                         return commentEntityToDto(commentsEntity);
                      }
                   )
                   . toList();
        postDto.setComments(commentDtos);
        return postDto;
    }

    public PostEntity postDtoToEntity(PostDto postDto){
        //converting model to entity without Tags
        PostEntity newPostEntity =modelMapper.map(postDto,PostEntity.class);
//        String [] tags= postDto.getTags().split(",");
        HashSet<String> tagsNameSet;
        if(postDto.getTags()==null || postDto.getTags().isEmpty()){
            newPostEntity.setTags(new HashSet<>());
            return newPostEntity;
        }
        tagsNameSet=new HashSet<>(
                Arrays.asList(
                        postDto.getTags().split(",")//tags
                )
        );
        HashSet<TagEntity> tagsList=new HashSet<>();
        for(String tagWithSpace:tagsNameSet){
            String tag=tagWithSpace.trim().toLowerCase();
            if(tag.length()>0)continue;// to check spaces also
            TagEntity tagEntity = tagRepository.findByName(tag);
            if(tagEntity==null){
                tagEntity=new TagEntity();
                tagEntity.setName(tag);
                tagRepository.save(tagEntity);
            }
            tagsList.add(tagEntity);
        }
        newPostEntity.setTags(tagsList);

        //comments;
        List<CommentsEntity> commentsEntities=new ArrayList<>();
        if(postDto.getComments()!=null){
            for(CommentDto commentDto:postDto.getComments()){
                commentsEntities.add(
                   commentsDtoToEntity(commentDto)
                );
            }
        }
        newPostEntity.setComments(commentsEntities);
        return newPostEntity;
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
        PostEntity postEntity=new PostEntity();
        postEntity.setId(commentDto.getPostId());
        commentsEntity.setPost(postEntity);
        return commentsEntity;
    }
}