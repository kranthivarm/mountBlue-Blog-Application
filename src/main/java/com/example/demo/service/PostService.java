package com.example.demo.service;

import com.example.demo.entities.PostEntity;
import com.example.demo.entities.TagEntity;
import com.example.demo.models.PostModel;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.TagRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public List<PostModel> findAll(){
        List<PostEntity>postEntities=postRepository.findAll();
        List<PostModel>posts=new ArrayList<>();
        for(PostEntity entity: postEntities){
            posts.add(
                modelMapper.map(entity,PostModel.class)
            );
        }
        return posts;
    }

    public PostModel findById(int id) {
//        return modelMapper.map(postRepository.findById(id),PostModel.class);
        PostEntity postEntity=postRepository.findById(id).orElse(null);
        if(postEntity==null)return null;
        PostModel postModel=modelMapper.map(postEntity,PostModel.class);

//        String tagString=String.join(',',postEntity.getTags());
        StringBuilder tagStr=new StringBuilder("");
        for(TagEntity tagEntity: postEntity.getTags()){
            tagStr.append(tagEntity.getName()+",");
        }
        postModel.setTags(tagStr.toString());
        return postModel;
    }
    @Transactional
    public PostModel createNewPost(PostModel postModel){
        //converting model to entity without Tags
        PostEntity newPostEntity =modelMapper.map(postModel,PostEntity.class);

        String [] tags=postModel.getTags().split(",");
        ArrayList<TagEntity>tagsList=new ArrayList<>();
        for(String tag:tags){
            if(tag.isBlank())continue;// to check spaces also
            TagEntity tagEntity = tagRepository.findByName(tag);
            if(tagEntity==null){
                tagEntity=new TagEntity();
                tagEntity.setName(tag);
                tagRepository.save(tagEntity);
            }
            tagsList.add(tagEntity);
        }
        newPostEntity.setTags(tagsList);
        //inserting entity
        PostEntity insertedEntity=postRepository.save(newPostEntity);
        //return model again;
        return modelMapper.map(insertedEntity,PostModel.class);
    }

    public  List<PostModel> findAllOrderByPublishedAt(){
        List<PostEntity>postEntities=postRepository.findAllByOrderByPublishedAtAsc();
        List<PostModel>posts=new ArrayList<>();
        for(PostEntity entity: postEntities) {
            posts.add(
                 modelMapper.map(entity,PostModel.class)
            );
        }
        return posts;
    }

    public void deleteById(int id){
        postRepository.deleteById(id);
    }

    public void updatePost(PostModel postModel){
        System.out.println("updatePost Service");
        postRepository.save(
            modelMapper.map(postModel,PostEntity.class)
        );
    }
}
