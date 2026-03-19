package com.example.demo.service;

import com.example.demo.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    private  final TagRepository tagRepository;
    TagService(TagRepository tagRepository){
        this.tagRepository=tagRepository;
    }
    public List<String> getAllTagNames(){
        return tagRepository.findAllTagNames();
    }
}
