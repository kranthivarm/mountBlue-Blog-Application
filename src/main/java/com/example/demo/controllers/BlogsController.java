package com.example.demo.controllers;

import com.example.demo.dtos.PostDto;
import com.example.demo.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/blogPost")
public class BlogsController {
    private final PostService postService;
    @Autowired
    BlogsController(PostService postService){
        this.postService=postService;
    }


    //creation of new blog post
    @GetMapping("/blogCreationForm")
    public String postFormForNewPost(Model model){
        System.out.println("creation form controller");
        model.addAttribute("post",new PostDto());
        return "blogCreationForm";
    }

    @PostMapping("/newPost")
    public  String createNewPost(@ModelAttribute PostDto postDto, Model model){
        System.out.println(" new post controller");
        PostDto insertedInstance = postService.createNewPost(postDto);
        if(insertedInstance!=null){
            //redirecting to singlePOstPost
            return "redirect:/blogPost/"+insertedInstance.getId();
        }
        else return "redirect:/blogPost/blogCreationForm";
    }

    //all blogs + sorting // sortField="publishedAt"&order="asc"
    @GetMapping("/allblogs")//all posts
    public  String getAllBlogs(
            Model model,
            @RequestParam(required = false)String search,
            @RequestParam(required = false)String authorName,
            @RequestParam(required = false)String tagNames,
            @RequestParam(defaultValue = "publishedAt")String sortField,
            @RequestParam(value="order",defaultValue = "desc")String order
    ){
        System.out.println("allBlogs controller"+search+authorName+tagNames);
        List<String>tagNamesList;
        if(tagNames!=null && !tagNames.isEmpty())tagNamesList=Arrays.asList(tagNames.split(","));
        else tagNamesList=new ArrayList<>();
        List<PostDto>postDtos;
        postDtos=postService.getFilteredPosts(
                search,authorName,tagNamesList,sortField,order
        );
        model.addAttribute("search",search);
        model.addAttribute("authorName",authorName);
        model.addAttribute("tagNames",tagNames);
        model.addAttribute("sortField",sortField);
        model.addAttribute("order",order);
        model.addAttribute("allPosts",postDtos);
        return "allBlogsPage";
    }

    //find by id
    @GetMapping("/{id}")
    public  String viewPost(@PathVariable String id,Model model ){
        System.out.println("single page controller");
        model.addAttribute("post",postService.findById(Integer.parseInt(id)));
        return "singlePostPage";
    }

    @GetMapping("/updatePostForm/{id}")
    public  String updatePostForm(@PathVariable String id, Model model){
        System.out.println("updateForm ctrlr");
        model.addAttribute("post",postService.findById(Integer.parseInt(id)));
        return "updatePostForm";
    }

    @PostMapping("/update")
    public String updateBlogPost(@ModelAttribute PostDto postDto){
        System.out.println(
                "updatePost ctrlr"+
                postDto.getTitle()+
                (postDto.getTags())
        );
        postService.updatePost(postDto);
        return "redirect:/blogPost/"+ postDto.getId();
    }

    //    @DeleteMapping("/deletePost/{id}")//browsers not supporting but we can
    @GetMapping("/deletePost/{id}")
    public String deletePost(@PathVariable String id){
        System.out.println("deleteBy Id Ctrlr");
        postService.deleteById(Integer.parseInt(id));
        return "redirect:/blogPost/allblogs";
    }
    //editing comment Form
    @GetMapping("/editCommentForm")
    public  String updateCommentById(
            @RequestParam(value = "commentId") String commentId,
            @RequestParam(value = "postId") String postId,
            Model model
    ){
        System.out.println("editCommentForm from blogCntr");
        model.addAttribute("post",postService.findById(Integer.parseInt(postId)));
        model.addAttribute("updateCommentId",Integer.parseInt(commentId));
        return "singlePostWithEditCommentForm";
    }
}