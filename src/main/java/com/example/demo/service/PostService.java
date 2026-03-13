package com.example.demo.service;

import com.example.demo.entities.PostEntity;
import com.example.demo.models.PostModel;
import com.example.demo.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository){
        this.postRepository=postRepository;
    }
    public List<PostModel> findAll(){
        List<PostEntity>postEntities=postRepository.findAll();
        List<PostModel>posts=new ArrayList<>();
        for(PostEntity entity: postEntities){
            PostModel postModel=new PostModel();
            postModel.setId(entity.getId());
            postModel.set_published(entity.is_published());
            postModel.setAuthor(entity.getAuthor());
            postModel.setContent(entity.getContent());
            postModel.setTitle(entity.getTitle());
            postModel.setExcerpt(entity.getExcerpt());
            postModel.setCreated_at(entity.getCreated_at());
            postModel.setPublished_at(entity.getPublished_at());
            postModel.setUpdated_at(entity.getUpdated_at());
            posts.add(postModel);
        }
        return posts;
//        List<PostModel>posts=null;
//        return posts;
        //repo call i think
//        or should i use any other layer
    }
}
