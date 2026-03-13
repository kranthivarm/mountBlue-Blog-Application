package com.example.demo.controllers;

import com.example.demo.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
//@RequestMapping("/posts")
public class BlogsController {
    private final PostService postService;
    @Autowired
    BlogsController(PostService postService){
        this.postService=postService;
    }
    @GetMapping("/allblogs")//all posts
    public  String getAllBlogs(Model model){
        model.addAttribute("allPosts",postService.findAll());
        return "allBlogsPage";
    }
    @GetMapping("/post/{id}")
    public  String viewPost(@PathVariable int id,Model model ){
        return "singlePost";
    }
}
