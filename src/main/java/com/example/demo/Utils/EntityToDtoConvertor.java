//package com.example.demo.Utils;
//
//import com.example.demo.dtos.PostDto;
//import com.example.demo.entities.PostEntity;
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class EntityToDtoConvertor {
//    private  final ModelMapper modelMapper;
//    @Autowired
//    EntityToDtoConvertor(ModelMapper mapper){
//        this.modelMapper=mapper;
//    }
//    public PostDto postEntityToDto(PostEntity postEntity){
//        PostDto postDto=modelMapper.map(postEntity,PostDto.class);
//        return postDto;
//    }
//}
